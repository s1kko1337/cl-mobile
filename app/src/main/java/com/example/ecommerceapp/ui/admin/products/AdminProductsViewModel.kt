package com.example.ecommerceapp.ui.admin.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.ProductCreateDTO
import com.example.ecommerceapp.data.model.ProductDTO
import com.example.ecommerceapp.data.repository.CategoryRepository
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProductsState(
    val products: List<ProductDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false
)

@HiltViewModel
class AdminProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProductsState())
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = productRepository.getProducts()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            products = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun showAddDialog() {
        _state.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _state.update { it.copy(showAddDialog = false) }
    }

    fun createProduct(name: String, description: String, price: Double, stock: Int, categoryId: Int, sku: String) {
        viewModelScope.launch {
            val product = ProductCreateDTO(name, description, price, stock, categoryId, sku)
            when (productRepository.createProduct(product)) {
                is Resource.Success -> {
                    loadProducts()
                    hideAddDialog()
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = "Ошибка создания товара") }
                }
                is Resource.Loading -> {}
            }
        }
    }


    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            when (productRepository.deleteProduct(id)) {
                is Resource.Success -> {
                    loadProducts()
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = "Ошибка удаления товара") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadProducts()
    }
}