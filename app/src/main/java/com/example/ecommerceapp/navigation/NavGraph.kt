package com.example.ecommerceapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ecommerceapp.ui.admin.dashboard.AdminDashboardScreen
import com.example.ecommerceapp.ui.admin.products.AdminProductsScreen
import com.example.ecommerceapp.ui.auth.login.LoginScreen
import com.example.ecommerceapp.ui.auth.register.RegisterScreen
import com.example.ecommerceapp.ui.customer.cart.CartScreen
import com.example.ecommerceapp.ui.customer.checkout.CheckoutScreen
import com.example.ecommerceapp.ui.customer.home.HomeScreen
import com.example.ecommerceapp.ui.customer.product.ProductDetailScreen
import com.example.ecommerceapp.ui.customer.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { role ->
                    val destination = if (role == "admin") {
                        Screen.AdminDashboard.route
                    } else {
                        Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() }
            )
        }

        // Customer screens
        composable(Screen.Home.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            ProductDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Admin screens
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToProducts = { navController.navigate(Screen.AdminProducts.route) },
                onNavigateToCategories = { navController.navigate(Screen.AdminCategories.route) },
                onNavigateToReviews = { navController.navigate(Screen.AdminReviews.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminProducts.route) {
            AdminProductsScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Screen.AdminProductEdit.createRoute(productId))
                }
            )
        }
    }
}