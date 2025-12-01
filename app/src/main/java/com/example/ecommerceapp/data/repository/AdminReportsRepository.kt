package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Репозиторий для работы с административными отчётами и аналитикой.
 *
 * Предоставляет методы для получения различной аналитики (продажи, выручка, топ товары),
 * данных для Dashboard и экспорта отчётов в CSV/Excel форматах.
 *
 * @property api Сервис API для выполнения сетевых запросов
 */
class AdminReportsRepository @Inject constructor(
    private val api: ApiService
) {
    /**
     * Получает дневную статистику продаж за указанный период.
     *
     * @param days Количество дней для выборки (по умолчанию 30)
     * @return Resource со списком DailySalesReportDTO или сообщением об ошибке
     */
    suspend fun getDailySales(days: Int = 30): Resource<List<DailySalesReportDTO>> {
        return try {
            val response = api.getDailySales(days)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения статистики")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает отчёт о продажах за определённый период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource с PeriodSalesReportDTO или сообщением об ошибке
     */
    suspend fun getPeriodSales(
        from: String?,
        to: String?
    ): Resource<PeriodSalesReportDTO> {
        return try {
            val response = api.getPeriodSales(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения отчета")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает помесячную статистику выручки.
     *
     * @param months Количество месяцев для выборки (по умолчанию 12)
     * @return Resource со списком MonthlyRevenueDTO или сообщением об ошибке
     */
    suspend fun getMonthlyRevenue(months: Int = 12): Resource<List<MonthlyRevenueDTO>> {
        return try {
            val response = api.getMonthlyRevenue(months)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения выручки")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает список топовых продуктов по продажам.
     *
     * @param limit Максимальное количество продуктов в списке (по умолчанию 10)
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource со списком TopProductDTO или сообщением об ошибке
     */
    suspend fun getTopProducts(
        limit: Int = 10,
        from: String?,
        to: String?
    ): Resource<List<TopProductDTO>> {
        return try {
            val response = api.getTopProducts(limit, from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения топ товаров")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает статистику продаж по категориям.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource со списком CategorySalesDTO или сообщением об ошибке
     */
    suspend fun getSalesByCategory(
        from: String?,
        to: String?
    ): Resource<List<CategorySalesDTO>> {
        return try {
            val response = api.getSalesByCategory(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения продаж по категориям")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает статистику по способам оплаты.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource со списком PaymentMethodStatsDTO или сообщением об ошибке
     */
    suspend fun getPaymentMethodStats(
        from: String?,
        to: String?
    ): Resource<List<PaymentMethodStatsDTO>> {
        return try {
            val response = api.getPaymentMethodStats(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения статистики по оплате")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает сводную статистику для админ-панели.
     *
     * @return Resource с DashboardSummaryDTO или сообщением об ошибке
     */
    suspend fun getDashboardSummary(): Resource<DashboardSummaryDTO> {
        return try {
            val response = api.getDashboardSummary()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения сводки")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Получает алерты для админ-панели.
     *
     * @return Resource со списком DashboardAlertDTO или сообщением об ошибке
     */
    suspend fun getDashboardAlerts(): Resource<List<DashboardAlertDTO>> {
        return try {
            val response = api.getDashboardAlerts()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка получения алертов")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует список товаров в формате CSV.
     *
     * @return Resource с ResponseBody (файл CSV) или сообщением об ошибке
     */
    suspend fun exportProductsCsv(): Resource<ResponseBody> {
        return try {
            val response = api.exportProductsCsv()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует список товаров в формате Excel.
     *
     * @return Resource с ResponseBody (файл Excel) или сообщением об ошибке
     */
    suspend fun exportProductsExcel(): Resource<ResponseBody> {
        return try {
            val response = api.exportProductsExcel()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует заказы в формате CSV за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource с ResponseBody (файл CSV) или сообщением об ошибке
     */
    suspend fun exportOrdersCsv(from: String?, to: String?): Resource<ResponseBody> {
        return try {
            val response = api.exportOrdersCsv(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует заказы в формате Excel за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource с ResponseBody (файл Excel) или сообщением об ошибке
     */
    suspend fun exportOrdersExcel(from: String?, to: String?): Resource<ResponseBody> {
        return try {
            val response = api.exportOrdersExcel(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует отчёт по продажам в формате CSV за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource с ResponseBody (файл CSV) или сообщением об ошибке
     */
    suspend fun exportSalesReportCsv(from: String?, to: String?): Resource<ResponseBody> {
        return try {
            val response = api.exportSalesReportCsv(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует отчёт по продажам в формате Excel за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Resource с ResponseBody (файл Excel) или сообщением об ошибке
     */
    suspend fun exportSalesReportExcel(from: String?, to: String?): Resource<ResponseBody> {
        return try {
            val response = api.exportSalesReportExcel(from, to)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует инвентаризацию (остатки товаров) в формате CSV.
     *
     * @return Resource с ResponseBody (файл CSV) или сообщением об ошибке
     */
    suspend fun exportInventoryCsv(): Resource<ResponseBody> {
        return try {
            val response = api.exportInventoryCsv()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Экспортирует инвентаризацию (остатки товаров) в формате Excel.
     *
     * @return Resource с ResponseBody (файл Excel) или сообщением об ошибке
     */
    suspend fun exportInventoryExcel(): Resource<ResponseBody> {
        return try {
            val response = api.exportInventoryExcel()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Ошибка экспорта")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }
}
