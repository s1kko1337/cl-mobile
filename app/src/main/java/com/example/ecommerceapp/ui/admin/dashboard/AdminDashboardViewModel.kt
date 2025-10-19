package com.example.ecommerceapp.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.data.repository.CategoryRepository
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val totalProducts: Int = 0,
    val totalCategories: Int = 0,
    val lowStockProducts: Int = 0
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats = _stats.asStateFlow()

    val username = authRepository.username

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val productsResult = productRepository.getProducts()
            val categoriesResult = categoryRepository.getCategories()

            if (productsResult is Resource.Success && categoriesResult is Resource.Success) {
                val products = productsResult.data ?: emptyList()
                _stats.value = DashboardStats(
                    totalProducts = products.size,
                    totalCategories = categoriesResult.data?.size ?: 0,
                    lowStockProducts = products.count { it.stock < 10 }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun refresh() {
        loadStats()
    }
}
