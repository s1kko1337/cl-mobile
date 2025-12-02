package com.example.ecommerceapp.ui.customer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана профиля пользователя.
 *
 * @property username Имя пользователя
 * @property email Email пользователя
 * @property role Роль пользователя (admin/customer)
 * @property isChangingPassword Индикатор процесса смены пароля
 * @property changePasswordSuccess Флаг успешной смены пароля
 * @property changePasswordError Сообщение об ошибке при смене пароля
 */
data class ProfileState(
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val isChangingPassword: Boolean = false,
    val changePasswordSuccess: Boolean = false,
    val changePasswordError: String? = null
)

/**
 * ViewModel для экрана профиля пользователя.
 *
 * Управляет отображением информации о пользователе, сменой пароля
 * и выходом из системы. Автоматически загружает данные пользователя
 * из локального хранилища при инициализации.
 *
 * @property authRepository Репозиторий для операций аутентификации
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())

    /**
     * Реактивный поток состояния профиля пользователя.
     */
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                authRepository.username,
                authRepository.email,
                authRepository.userRole
            ) { username, email, role ->
                _state.update { it.copy(
                    username = username ?: "",
                    email = email ?: "",
                    role = role ?: ""
                )}
            }.collect()
        }
    }

    /**
     * Изменяет пароль пользователя.
     *
     * Проверяет соответствие нового пароля и его подтверждения,
     * отправляет запрос на сервер и обновляет состояние.
     *
     * @param currentPassword Текущий пароль пользователя
     * @param newPassword Новый пароль
     * @param confirmPassword Подтверждение нового пароля
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(
                isChangingPassword = true,
                changePasswordError = null,
                changePasswordSuccess = false
            )}

            when (val result = authRepository.changePassword(currentPassword, newPassword, confirmPassword)) {
                is Resource.Success -> {
                    _state.update { it.copy(
                        isChangingPassword = false,
                        changePasswordSuccess = true
                    )}
                }
                is Resource.Error -> {
                    _state.update { it.copy(
                        isChangingPassword = false,
                        changePasswordError = result.message
                    )}
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Очищает состояние смены пароля (сообщения об успехе и ошибках).
     */
    fun clearChangePasswordState() {
        _state.update { it.copy(
            changePasswordSuccess = false,
            changePasswordError = null
        )}
    }

    /**
     * Выполняет выход пользователя из системы.
     *
     * Очищает данные аутентификации в локальном хранилище.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}