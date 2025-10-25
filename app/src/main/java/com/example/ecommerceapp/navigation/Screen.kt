package com.example.ecommerceapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    // Customer screens
    object Home : Screen("home")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Int) = "product/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Profile : Screen("profile")
    object Orders : Screen("orders")
    object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: Int) = "order/$orderId"
    }

    // Admin screens
    object AdminDashboard : Screen("admin/dashboard")
    object AdminProducts : Screen("admin/products")
    object AdminProductEdit : Screen("admin/products/{productId}") {
        fun createRoute(productId: Int) = "admin/products/$productId"
    }
    object AdminCategories : Screen("admin/categories")
    object AdminReviews : Screen("admin/reviews")
    object AdminOrders : Screen("admin/orders")
    object AdminOrderDetail : Screen("admin/orders/{orderId}") {
        fun createRoute(orderId: Int) = "admin/orders/$orderId"
    }
}