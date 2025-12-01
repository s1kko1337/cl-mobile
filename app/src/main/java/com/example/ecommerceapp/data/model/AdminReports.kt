package com.example.ecommerceapp.data.model

/**
 * DTO для дневной статистики продаж.
 *
 * @property date Дата в формате ISO 8601
 * @property ordersCount Количество заказов за день
 * @property totalRevenue Общая выручка за день
 * @property itemsSold Количество проданных товаров
 * @property averageOrderValue Средний чек
 */
data class DailySalesReportDTO(
    val date: String,
    val ordersCount: Int,
    val totalRevenue: Double,
    val itemsSold: Int,
    val averageOrderValue: Double
)

/**
 * DTO для отчета о продажах за определенный период.
 *
 * @property fromDate Начальная дата периода
 * @property toDate Конечная дата периода
 * @property totalOrders Общее количество заказов за период
 * @property totalRevenue Общая выручка за период
 * @property totalItemsSold Общее количество проданных товаров
 * @property averageOrderValue Средний чек за период
 * @property dailySales Список дневной статистики продаж
 */
data class PeriodSalesReportDTO(
    val fromDate: String,
    val toDate: String,
    val totalOrders: Int,
    val totalRevenue: Double,
    val totalItemsSold: Int,
    val averageOrderValue: Double,
    val dailySales: List<DailySalesReportDTO>
)

/**
 * DTO для помесячной статистики выручки.
 *
 * @property year Год
 * @property month Месяц (1-12)
 * @property monthName Название месяца
 * @property ordersCount Количество заказов за месяц
 * @property totalRevenue Общая выручка за месяц
 * @property itemsSold Количество проданных товаров за месяц
 */
data class MonthlyRevenueDTO(
    val year: Int,
    val month: Int,
    val monthName: String,
    val ordersCount: Int,
    val totalRevenue: Double,
    val itemsSold: Int
)

/**
 * DTO для представления топового продукта в отчетах.
 *
 * @property productId ID продукта
 * @property productName Название продукта
 * @property sku Артикул товара
 * @property totalQuantitySold Всего продано единиц
 * @property totalRevenue Общая выручка от продукта
 * @property ordersCount В скольких заказах присутствовал продукт
 */
data class TopProductDTO(
    val productId: Int,
    val productName: String,
    val sku: String,
    val totalQuantitySold: Int,
    val totalRevenue: Double,
    val ordersCount: Int
)

/**
 * DTO для статистики продаж по категориям.
 *
 * @property categoryId ID категории
 * @property categoryName Название категории
 * @property productsCount Количество уникальных товаров в категории
 * @property totalQuantitySold Всего продано единиц из категории
 * @property totalRevenue Общая выручка от категории
 * @property ordersCount Количество заказов с товарами из категории
 * @property averagePrice Средняя цена товара в категории
 */
data class CategorySalesDTO(
    val categoryId: Int,
    val categoryName: String,
    val productsCount: Int,
    val totalQuantitySold: Int,
    val totalRevenue: Double,
    val ordersCount: Int,
    val averagePrice: Double
)

/**
 * DTO для статистики по способам оплаты.
 *
 * @property paymentMethod Способ оплаты ("Card" или "Cash")
 * @property ordersCount Количество заказов с данным способом оплаты
 * @property totalRevenue Общая выручка от данного способа оплаты
 * @property percentage Процент от общей выручки
 */
data class PaymentMethodStatsDTO(
    val paymentMethod: String,
    val ordersCount: Int,
    val totalRevenue: Double,
    val percentage: Double
)

/**
 * DTO для представления недавнего заказа в Dashboard.
 *
 * @property id ID заказа
 * @property customerName Имя покупателя
 * @property totalAmount Общая сумма заказа
 * @property status Статус заказа
 * @property createdAt Дата и время создания заказа
 */
data class RecentOrderDTO(
    val id: Int,
    val customerName: String,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)

/**
 * DTO для сводной статистики админ-панели.
 *
 * Содержит общую статистику, показатели за разные периоды,
 * ключевые KPI метрики и последние заказы.
 *
 * @property totalOrders Общее количество заказов
 * @property totalProducts Общее количество продуктов
 * @property totalCategories Общее количество категорий
 * @property totalUsers Общее количество пользователей
 * @property totalRevenue Общая выручка
 * @property todayOrders Количество заказов за сегодня
 * @property todayRevenue Выручка за сегодня
 * @property weekOrders Количество заказов за неделю
 * @property weekRevenue Выручка за неделю
 * @property monthOrders Количество заказов за месяц
 * @property monthRevenue Выручка за месяц
 * @property averageOrderValue Средний чек
 * @property lowStockProductsCount Количество товаров с остатком ≤10
 * @property outOfStockProductsCount Количество товаров с нулевым остатком
 * @property pendingOrdersCount Количество необработанных заказов
 * @property ordersByStatus Распределение заказов по статусам
 * @property recentOrders Последние 5 заказов
 */
data class DashboardSummaryDTO(
    val totalOrders: Int,
    val totalProducts: Int,
    val totalCategories: Int,
    val totalUsers: Int,
    val totalRevenue: Double,
    val todayOrders: Int,
    val todayRevenue: Double,
    val weekOrders: Int,
    val weekRevenue: Double,
    val monthOrders: Int,
    val monthRevenue: Double,
    val averageOrderValue: Double,
    val lowStockProductsCount: Int,
    val outOfStockProductsCount: Int,
    val pendingOrdersCount: Int,
    val ordersByStatus: Map<String, Int>,
    val recentOrders: List<RecentOrderDTO>
)

/**
 * DTO для алертов в Dashboard.
 *
 * @property type Тип алерта ("warning", "info", "error")
 * @property message Текст сообщения
 * @property count Количество элементов, связанных с алертом
 */
data class DashboardAlertDTO(
    val type: String,
    val message: String,
    val count: Int
)
