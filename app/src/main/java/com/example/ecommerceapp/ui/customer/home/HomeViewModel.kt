package com.example.ecommerceapp.ui.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CategoryDTO
import com.example.ecommerceapp.data.model.ProductDTO
import com.example.ecommerceapp.data.repository.CartRepository
import com.example.ecommerceapp.data.repository.CategoryRepository
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val products: List<ProductDTO> = emptyList(),
    val categories: List<CategoryDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategoryId: Int? = null,
    val cartItemCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadData()
        observeCartCount()
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _state.update { it.copy(cartItemCount = count) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val categoriesResult = categoryRepository.getCategories()
            val productsResult = productRepository.getProducts()

            _state.update {
                it.copy(
                    categories = if (categoriesResult is Resource.Success)
                        categoriesResult.data ?: emptyList() else emptyList(),
                    products = if (productsResult is Resource.Success)
                        productsResult.data ?: emptyList() else emptyList(),
                    isLoading = false,
                    error = when {
                        categoriesResult is Resource.Error -> categoriesResult.message
                        productsResult is Resource.Error -> productsResult.message
                        else -> null
                    }
                )
            }
        }
    }

    fun filterByCategory(categoryId: Int?) {
        viewModelScope.launch {
            _state.update { it.copy(selectedCategoryId = categoryId, isLoading = true) }

            val result = if (categoryId != null) {
                categoryRepository.getCategoryProducts(categoryId)
            } else {
                productRepository.getProducts()
            }

            _state.update {
                it.copy(
                    products = if (result is Resource.Success) result.data ?: emptyList() else emptyList(),
                    isLoading = false,
                    error = if (result is Resource.Error) result.message else null
                )
            }
        }
    }

    fun refresh() {
        loadData()
    }
}