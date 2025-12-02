package com.example.ecommerceapp.navigation

/**
 * Sealed класс, определяющий все маршруты навигации в приложении.
 *
 * Содержит объекты для:
 * - Экранов аутентификации (Login, Register)
 * - Пользовательских экранов (Home, Cart, Profile, Orders и др.)
 * - Административных экранов (Dashboard, Products, Analytics и др.)
 *
 * Некоторые экраны содержат параметры маршрута и предоставляют
 * вспомогательные методы createRoute() для формирования маршрутов с параметрами.
 *
 * @property route Строка маршрута для Jetpack Compose Navigation
 */
sealed class Screen(val route: String) {
    /**
     * Экран входа в систему.
     */
    object Login : Screen("login")

    /**
     * Экран регистрации нового пользователя.
     */
    object Register : Screen("register")

    /**
     * Главный экран приложения с каталогом товаров.
     */
    object Home : Screen("home")

    /**
     * Экран детальной информации о продукте.
     *
     * @property createRoute Создаёт маршрут с указанным ID продукта
     */
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Int) = "product/$productId"
    }

    /**
     * Экран корзины покупок.
     */
    object Cart : Screen("cart")

    /**
     * Экран оформления заказа.
     */
    object Checkout : Screen("checkout")

    /**
     * Экран профиля пользователя.
     */
    object Profile : Screen("profile")

    /**
     * Экран настроек приложения.
     */
    object Settings : Screen("settings")

    /**
     * Экран информации о приложении.
     */
    object About : Screen("about")

    /**
     * Экран списка заказов пользователя.
     */
    object Orders : Screen("orders")

    /**
     * Экран детальной информации о заказе.
     *
     * @property createRoute Создаёт маршрут с указанным ID заказа
     */
    object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: Int) = "order/$orderId"
    }

    /**
     * Экран выбора адреса доставки на карте Yandex Maps.
     */
    object MapAddressPicker : Screen("map/address-picker")

    /**
     * Главная панель администратора с общей статистикой.
     */
    object AdminDashboard : Screen("admin/dashboard")

    /**
     * Экран управления продуктами (список всех товаров).
     */
    object AdminProducts : Screen("admin/products")

    /**
     * Экран редактирования продукта.
     *
     * @property createRoute Создаёт маршрут с указанным ID продукта
     */
    object AdminProductEdit : Screen("admin/products/{productId}") {
        fun createRoute(productId: Int) = "admin/products/$productId"
    }

    /**
     * Экран управления категориями товаров.
     */
    object AdminCategories : Screen("admin/categories")

    /**
     * Экран управления отзывами на товары.
     */
    object AdminReviews : Screen("admin/reviews")

    /**
     * Экран управления заказами.
     */
    object AdminOrders : Screen("admin/orders")

    /**
     * Экран детальной информации о заказе в админ-панели.
     *
     * @property createRoute Создаёт маршрут с указанным ID заказа
     */
    object AdminOrderDetail : Screen("admin/orders/{orderId}") {
        fun createRoute(orderId: Int) = "admin/orders/$orderId"
    }

    /**
     * Экран аналитики и отчётов для администратора.
     */
    object AdminAnalytics : Screen("admin/analytics")

    /**
     * Экран экспорта данных (CSV, Excel).
     */
    object AdminExport : Screen("admin/export")
}