package com.example.ecommerceapp.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.DashboardAlertDTO
import com.example.ecommerceapp.data.model.DashboardSummaryDTO
import com.example.ecommerceapp.data.model.RecentOrderDTO
import com.example.ecommerceapp.data.repository.AdminReportsRepository
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние админской панели управления.
 *
 * @property isLoading Индикатор загрузки данных панели
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property dashboardSummary Сводная статистика для отображения на панели
 * @property alerts Список уведомлений и предупреждений
 * @property isChangingPassword Индикатор процесса изменения пароля
 * @property changePasswordSuccess Флаг успешного изменения пароля
 * @property changePasswordError Сообщение об ошибке при изменении пароля
 */
data class AdminDashboardState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dashboardSummary: DashboardSummaryDTO? = null,
    val alerts: List<DashboardAlertDTO> = emptyList(),
    val isChangingPassword: Boolean = false,
    val changePasswordSuccess: Boolean = false,
    val changePasswordError: String? = null
)

/**
 * ViewModel для главной панели администратора.
 *
 * Управляет загрузкой и отображением сводной статистики,
 * уведомлений, а также функциями выхода и изменения пароля администратора.
 *
 * @property reportsRepository Репозиторий для получения отчётов и статистики
 * @property authRepository Репозиторий для операций аутентификации
 */
@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val reportsRepository: AdminReportsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())

    /**
     * Реактивный поток состояния панели администратора.
     */
    val state = _state.asStateFlow()

    /**
     * Имя пользователя текущего администратора.
     */
    val username = authRepository.username

    init {
        loadDashboard()
    }

    /**
     * Загружает данные для панели администратора.
     *
     * Последовательно загружает сводную статистику и список уведомлений.
     */
    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val summaryResult = reportsRepository.getDashboardSummary()) {
                is Resource.Success -> {
                    _state.update { it.copy(dashboardSummary = summaryResult.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = summaryResult.message) }
                }
                is Resource.Loading -> {}
            }

            when (val alertsResult = reportsRepository.getDashboardAlerts()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            alerts = alertsResult.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            error = alertsResult.message,
                            isLoading = false
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Выполняет выход администратора из системы.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    /**
     * Обновляет данные панели администратора.
     */
    fun refresh() {
        loadDashboard()
    }

    /**
     * Изменяет пароль текущего администратора.
     *
     * Проверяет соответствие нового пароля и его подтверждения,
     * отправляет запрос на изменение пароля и обновляет состояние.
     *
     * @param currentPassword Текущий пароль администратора
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
     * Очищает состояние процесса изменения пароля.
     *
     * Сбрасывает флаги успеха и ошибки изменения пароля.
     */
    fun clearChangePasswordState() {
        _state.update { it.copy(
            changePasswordSuccess = false,
            changePasswordError = null
        )}
    }
}
