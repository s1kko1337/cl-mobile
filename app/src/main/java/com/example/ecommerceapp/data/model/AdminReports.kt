package com.example.ecommerceapp.data.model

// Дневная статистика продаж
data class DailySalesReportDTO(
    val date: String,              // ISO 8601 формат
    val ordersCount: Int,          // Количество заказов
    val totalRevenue: Double,      // Общая выручка
    val itemsSold: Int,            // Продано товаров
    val averageOrderValue: Double  // Средний чек
)

// Отчет за период
data class PeriodSalesReportDTO(
    val fromDate: String,
    val toDate: String,
    val totalOrders: Int,
    val totalRevenue: Double,
    val totalItemsSold: Int,
    val averageOrderValue: Double,
    val dailySales: List<DailySalesReportDTO>
)

// Помесячная выручка
data class MonthlyRevenueDTO(
    val year: Int,
    val month: Int,             // 1-12
    val monthName: String,      // Название месяца
    val ordersCount: Int,
    val totalRevenue: Double,
    val itemsSold: Int
)

// Топ товары
data class TopProductDTO(
    val productId: Int,
    val productName: String,
    val sku: String,                  // Артикул товара
    val totalQuantitySold: Int,       // Всего продано штук
    val totalRevenue: Double,         // Общая выручка
    val ordersCount: Int              // В скольких заказах
)

// Продажи по категориям
data class CategorySalesDTO(
    val categoryId: Int,
    val categoryName: String,
    val productsCount: Int,           // Количество уникальных товаров
    val totalQuantitySold: Int,       // Всего продано единиц
    val totalRevenue: Double,         // Общая выручка
    val ordersCount: Int,             // Количество заказов
    val averagePrice: Double          // Средняя цена товара
)

// Статистика по способам оплаты
data class PaymentMethodStatsDTO(
    val paymentMethod: String,    // "Card" или "Cash"
    val ordersCount: Int,
    val totalRevenue: Double,
    val percentage: Double        // Процент от общей выручки
)

// Последний заказ (для Dashboard)
data class RecentOrderDTO(
    val id: Int,
    val customerName: String,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)

// Dashboard сводка
data class DashboardSummaryDTO(
    // Общая статистика
    val totalOrders: Int,
    val totalProducts: Int,
    val totalCategories: Int,
    val totalUsers: Int,
    val totalRevenue: Double,

    // За сегодня
    val todayOrders: Int,
    val todayRevenue: Double,

    // За неделю
    val weekOrders: Int,
    val weekRevenue: Double,

    // За месяц
    val monthOrders: Int,
    val monthRevenue: Double,

    // KPI
    val averageOrderValue: Double,          // Средний чек
    val lowStockProductsCount: Int,         // Товаров с остатком ≤10
    val outOfStockProductsCount: Int,       // Товаров с нулевым остатком
    val pendingOrdersCount: Int,            // Необработанных заказов

    // Статусы
    val ordersByStatus: Map<String, Int>,   // Заказы по статусам

    // Последние заказы
    val recentOrders: List<RecentOrderDTO>  // Последние 5 заказов
)

// Алерты для Dashboard
data class DashboardAlertDTO(
    val type: String,      // "warning", "info", "error"
    val message: String,   // Текст сообщения
    val count: Int         // Количество элементов
)
