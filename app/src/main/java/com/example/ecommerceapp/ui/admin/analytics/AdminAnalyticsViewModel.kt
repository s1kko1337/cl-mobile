package com.example.ecommerceapp.ui.admin.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.repository.AdminReportsRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminAnalyticsState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Дневная статистика
    val dailySales: List<DailySalesReportDTO> = emptyList(),

    // Помесячная выручка
    val monthlyRevenue: List<MonthlyRevenueDTO> = emptyList(),

    // Топ товары
    val topProducts: List<TopProductDTO> = emptyList(),

    // Продажи по категориям
    val categorySales: List<CategorySalesDTO> = emptyList(),

    // Статистика по способам оплаты
    val paymentMethodStats: List<PaymentMethodStatsDTO> = emptyList(),

    // Выбранный период для фильтрации
    val selectedDays: Int = 30,
    val selectedMonths: Int = 12,
    val topProductsLimit: Int = 10
)

@HiltViewModel
class AdminAnalyticsViewModel @Inject constructor(
    private val reportsRepository: AdminReportsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAnalyticsState())
    val state = _state.asStateFlow()

    init {
        loadAllAnalytics()
    }

    fun loadAllAnalytics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Загружаем дневную статистику
            loadDailySales(state.value.selectedDays)

            // Загружаем помесячную выручку
            loadMonthlyRevenue(state.value.selectedMonths)

            // Загружаем топ товары
            loadTopProducts(state.value.topProductsLimit)

            // Загружаем продажи по категориям
            loadSalesByCategory()

            // Загружаем статистику по способам оплаты
            loadPaymentMethodStats()

            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadDailySales(days: Int) {
        when (val result = reportsRepository.getDailySales(days)) {
            is Resource.Success -> {
                _state.update { it.copy(dailySales = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {
                // Уже в loading
            }
        }
    }

    private suspend fun loadMonthlyRevenue(months: Int) {
        when (val result = reportsRepository.getMonthlyRevenue(months)) {
            is Resource.Success -> {
                _state.update { it.copy(monthlyRevenue = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {
                // Уже в loading
            }
        }
    }

    private suspend fun loadTopProducts(limit: Int) {
        when (val result = reportsRepository.getTopProducts(limit, null, null)) {
            is Resource.Success -> {
                _state.update { it.copy(topProducts = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {
                // Уже в loading
            }
        }
    }

    private suspend fun loadSalesByCategory() {
        when (val result = reportsRepository.getSalesByCategory(null, null)) {
            is Resource.Success -> {
                _state.update { it.copy(categorySales = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {
                // Уже в loading
            }
        }
    }

    private suspend fun loadPaymentMethodStats() {
        when (val result = reportsRepository.getPaymentMethodStats(null, null)) {
            is Resource.Success -> {
                _state.update { it.copy(paymentMethodStats = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {
                // Уже в loading
            }
        }
    }

    fun setDaysFilter(days: Int) {
        _state.update { it.copy(selectedDays = days) }
        viewModelScope.launch {
            loadDailySales(days)
        }
    }

    fun setMonthsFilter(months: Int) {
        _state.update { it.copy(selectedMonths = months) }
        viewModelScope.launch {
            loadMonthlyRevenue(months)
        }
    }

    fun setTopProductsLimit(limit: Int) {
        _state.update { it.copy(topProductsLimit = limit) }
        viewModelScope.launch {
            loadTopProducts(limit)
        }
    }

    fun refresh() {
        loadAllAnalytics()
    }
}
