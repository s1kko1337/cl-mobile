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

data class AdminDashboardState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dashboardSummary: DashboardSummaryDTO? = null,
    val alerts: List<DashboardAlertDTO> = emptyList()
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val reportsRepository: AdminReportsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state = _state.asStateFlow()

    val username = authRepository.username

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Загружаем сводку dashboard
            when (val summaryResult = reportsRepository.getDashboardSummary()) {
                is Resource.Success -> {
                    _state.update { it.copy(dashboardSummary = summaryResult.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = summaryResult.message) }
                }
                is Resource.Loading -> {
                    // Уже в loading
                }
            }

            // Загружаем алерты
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
                is Resource.Loading -> {
                    // Уже в loading
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
