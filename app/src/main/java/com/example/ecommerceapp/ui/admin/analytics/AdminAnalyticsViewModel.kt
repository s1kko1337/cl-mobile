package com.example.ecommerceapp.ui.admin.analytics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.repository.AdminReportsRepository
import com.example.ecommerceapp.util.PdfReportGenerator
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Состояние экрана аналитики администратора.
 *
 * @property isLoading Индикатор загрузки данных
 * @property error Сообщение об ошибке
 * @property dailySales Дневная статистика продаж
 * @property monthlyRevenue Помесячная выручка
 * @property topProducts Топ товаров по продажам
 * @property categorySales Продажи по категориям
 * @property paymentMethodStats Статистика по способам оплаты
 * @property selectedDays Выбранный период дней для фильтрации
 * @property selectedMonths Выбранный период месяцев для фильтрации
 * @property topProductsLimit Лимит топ товаров
 * @property isGeneratingPdf Индикатор генерации PDF
 * @property pdfGenerationSuccess Сообщение об успешной генерации PDF
 * @property pdfGenerationError Ошибка генерации PDF
 */
data class AdminAnalyticsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dailySales: List<DailySalesReportDTO> = emptyList(),
    val monthlyRevenue: List<MonthlyRevenueDTO> = emptyList(),
    val topProducts: List<TopProductDTO> = emptyList(),
    val categorySales: List<CategorySalesDTO> = emptyList(),
    val paymentMethodStats: List<PaymentMethodStatsDTO> = emptyList(),
    val selectedDays: Int = 30,
    val selectedMonths: Int = 12,
    val topProductsLimit: Int = 10,
    val isGeneratingPdf: Boolean = false,
    val pdfGenerationSuccess: String? = null,
    val pdfGenerationError: String? = null
)

/**
 * ViewModel для экрана аналитики администратора.
 *
 * Управляет загрузкой и отображением различных видов статистики и аналитики,
 * а также генерацией PDF отчётов.
 *
 * @property reportsRepository Репозиторий для получения отчётов
 * @property context Контекст приложения для генерации PDF
 */
@HiltViewModel
class AdminAnalyticsViewModel @Inject constructor(
    private val reportsRepository: AdminReportsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAnalyticsState())

    /** Реактивный поток состояния экрана аналитики */
    val state = _state.asStateFlow()

    private val pdfGenerator = PdfReportGenerator(context)

    init {
        loadAllAnalytics()
    }

    /** Загружает все виды аналитики с учётом текущих фильтров */
    fun loadAllAnalytics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            loadDailySales(state.value.selectedDays)
            loadMonthlyRevenue(state.value.selectedMonths)
            loadTopProducts(state.value.topProductsLimit)
            loadSalesByCategory()
            loadPaymentMethodStats()
            _state.update { it.copy(isLoading = false) }
        }
    }

    /** Загружает дневную статистику продаж за указанный период. @param days Количество дней */
    private suspend fun loadDailySales(days: Int) {
        when (val result = reportsRepository.getDailySales(days)) {
            is Resource.Success -> {
                _state.update { it.copy(dailySales = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {}
        }
    }

    /** Загружает помесячную выручку. @param months Количество месяцев */
    private suspend fun loadMonthlyRevenue(months: Int) {
        when (val result = reportsRepository.getMonthlyRevenue(months)) {
            is Resource.Success -> {
                _state.update { it.copy(monthlyRevenue = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {}
        }
    }

    /** Загружает топ товаров. @param limit Количество топ товаров */
    private suspend fun loadTopProducts(limit: Int) {
        when (val result = reportsRepository.getTopProducts(limit, null, null)) {
            is Resource.Success -> {
                _state.update { it.copy(topProducts = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {}
        }
    }

    /** Загружает продажи по категориям */
    private suspend fun loadSalesByCategory() {
        when (val result = reportsRepository.getSalesByCategory(null, null)) {
            is Resource.Success -> {
                _state.update { it.copy(categorySales = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {}
        }
    }

    /** Загружает статистику по способам оплаты */
    private suspend fun loadPaymentMethodStats() {
        when (val result = reportsRepository.getPaymentMethodStats(null, null)) {
            is Resource.Success -> {
                _state.update { it.copy(paymentMethodStats = result.data ?: emptyList()) }
            }
            is Resource.Error -> {
                _state.update { it.copy(error = result.message) }
            }
            is Resource.Loading -> {}
        }
    }

    /** Устанавливает фильтр по дням. @param days Количество дней */
    fun setDaysFilter(days: Int) {
        _state.update { it.copy(selectedDays = days) }
        viewModelScope.launch {
            loadDailySales(days)
        }
    }

    /** Устанавливает фильтр по месяцам. @param months Количество месяцев */
    fun setMonthsFilter(months: Int) {
        _state.update { it.copy(selectedMonths = months) }
        viewModelScope.launch {
            loadMonthlyRevenue(months)
        }
    }

    /** Устанавливает лимит топ товаров. @param limit Количество топ товаров */
    fun setTopProductsLimit(limit: Int) {
        _state.update { it.copy(topProductsLimit = limit) }
        viewModelScope.launch {
            loadTopProducts(limit)
        }
    }

    /** Обновляет все данные аналитики */
    fun refresh() {
        loadAllAnalytics()
    }

    /** Генерирует PDF отчёт с текущими данными аналитики */
    fun generatePdfReport() {
        viewModelScope.launch {
            _state.update { it.copy(
                isGeneratingPdf = true,
                pdfGenerationSuccess = null,
                pdfGenerationError = null
            ) }

            try {
                val file = withContext(Dispatchers.IO) {
                    pdfGenerator.generateAnalyticsReport(
                        dailySales = state.value.dailySales,
                        monthlyRevenue = state.value.monthlyRevenue,
                        topProducts = state.value.topProducts,
                        categorySales = state.value.categorySales,
                        paymentMethodStats = state.value.paymentMethodStats,
                        selectedDays = state.value.selectedDays,
                        topProductsLimit = state.value.topProductsLimit
                    )
                }

                if (file != null) {
                    _state.update { it.copy(
                        isGeneratingPdf = false,
                        pdfGenerationSuccess = "PDF отчет сохранен: ${file.name}"
                    ) }
                } else {
                    _state.update { it.copy(
                        isGeneratingPdf = false,
                        pdfGenerationError = "Не удалось создать PDF отчет"
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isGeneratingPdf = false,
                    pdfGenerationError = e.message ?: "Ошибка при создании PDF"
                ) }
            }
        }
    }

    /**
     * Очищает сообщения о генерации PDF отчёта.
     *
     * Сбрасывает флаги успеха и ошибки генерации PDF.
     */
    fun clearPdfMessages() {
        _state.update { it.copy(
            pdfGenerationSuccess = null,
            pdfGenerationError = null
        ) }
    }
}
