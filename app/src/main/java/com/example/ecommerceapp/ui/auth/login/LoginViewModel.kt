package com.example.ecommerceapp.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана входа в систему.
 *
 * @property isLoading Индикатор загрузки (выполняется ли запрос авторизации)
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property role Роль пользователя после успешного входа (admin/customer)
 */
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val role: String? = null
)

/**
 * ViewModel для экрана входа в систему.
 *
 * Управляет процессом аутентификации пользователя, обрабатывает
 * результаты запроса на вход и обновляет состояние UI.
 *
 * @property authRepository Репозиторий для выполнения операций аутентификации
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())

    /**
     * Реактивный поток состояния экрана входа.
     */
    val state = _state.asStateFlow()

    /**
     * Выполняет вход пользователя в систему.
     *
     * Отправляет запрос на аутентификацию с указанными учётными данными
     * и обновляет состояние в зависимости от результата.
     *
     * @param username Имя пользователя или email
     * @param password Пароль пользователя
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState(isLoading = true)
            when (val result = authRepository.login(username, password)) {
                is Resource.Success -> {
                    _state.value = LoginState(role = result.data?.user?.role)
                }
                is Resource.Error -> {
                    _state.value = LoginState(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Очищает сообщение об ошибке из состояния.
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}