package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import okhttp3.ResponseBody
import javax.inject.Inject

class AdminReportsRepository @Inject constructor(
    private val api: ApiService
) {
    // Аналитика - Дневная статистика продаж
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

    // Аналитика - Отчет за период
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

    // Аналитика - Помесячная выручка
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

    // Аналитика - Топ товары
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

    // Аналитика - Продажи по категориям
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

    // Аналитика - Статистика по способам оплаты
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

    // Dashboard - Сводка
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

    // Dashboard - Алерты
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

    // Экспорт - Товары CSV
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

    // Экспорт - Товары Excel
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

    // Экспорт - Заказы CSV
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

    // Экспорт - Заказы Excel
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

    // Экспорт - Отчет по продажам CSV
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

    // Экспорт - Отчет по продажам Excel
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

    // Экспорт - Инвентаризация CSV
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

    // Экспорт - Инвентаризация Excel
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
