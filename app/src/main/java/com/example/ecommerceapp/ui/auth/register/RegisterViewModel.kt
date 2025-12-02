package com.example.ecommerceapp.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана регистрации нового пользователя.
 *
 * @property isLoading Индикатор загрузки (выполняется ли запрос регистрации)
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property success Флаг успешной регистрации
 */
data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

/**
 * ViewModel для экрана регистрации нового пользователя.
 *
 * Управляет процессом регистрации, валидирует данные пользователя,
 * обрабатывает результаты запроса и обновляет состояние UI.
 *
 * @property authRepository Репозиторий для выполнения операций регистрации
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())

    /**
     * Реактивный поток состояния экрана регистрации.
     */
    val state = _state.asStateFlow()

    /**
     * Регистрирует нового пользователя в системе.
     *
     * Отправляет запрос на регистрацию с указанными данными
     * и обновляет состояние в зависимости от результата.
     *
     * @param username Имя пользователя
     * @param email Email пользователя
     * @param password Пароль пользователя
     */
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.register(username, email, password)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, success = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}