package com.example.ecommerceapp.ui.admin.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CategoryDTO
import com.example.ecommerceapp.data.model.ProductCreateDTO
import com.example.ecommerceapp.data.repository.CategoryRepository
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddProductDialogState(
    val name: String = "",
    val sku: String = "",
    val description: String = "",
    val price: String = "",
    val stockQuantity: String = "",
    val categoryId: Int? = null,
    val categories: List<CategoryDTO> = emptyList(),
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val skuError: String? = null,
    val priceError: String? = null,
    val stockError: String? = null,
    val categoryError: String? = null,
    val generalError: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AddProductDialogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductDialogState())
    val state: StateFlow<AddProductDialogState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = categoryRepository.getCategories()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(categories = result.data ?: emptyList())
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(generalError = "Не удалось загрузить категории")
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onNameChange(value: String) {
        _state.update {
            it.copy(
                name = value,
                nameError = null
            )
        }
    }

    fun onSkuChange(value: String) {
        _state.update {
            it.copy(
                sku = value,
                skuError = null
            )
        }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onPriceChange(value: String) {
        _state.update {
            it.copy(
                price = value,
                priceError = null
            )
        }
    }

    fun onStockQuantityChange(value: String) {
        _state.update {
            it.copy(
                stockQuantity = value,
                stockError = null
            )
        }
    }

    fun onCategorySelected(categoryId: Int) {
        _state.update {
            it.copy(
                categoryId = categoryId,
                categoryError = null
            )
        }
    }

    fun createProduct() {
        val currentState = _state.value

        // Валидация
        var hasErrors = false
        var nameError: String? = null
        var skuError: String? = null
        var priceError: String? = null
        var stockError: String? = null
        var categoryError: String? = null

        if (currentState.name.isBlank()) {
            nameError = "Введите название"
            hasErrors = true
        }

        if (currentState.sku.isBlank()) {
            skuError = "Введите артикул"
            hasErrors = true
        }

        val price = currentState.price.toDoubleOrNull()
        if (price == null || price <= 0) {
            priceError = "Введите корректную цену"
            hasErrors = true
        }

        val stock = currentState.stockQuantity.toIntOrNull()
        if (stock == null || stock < 0) {
            stockError = "Введите корректный остаток"
            hasErrors = true
        }

        if (currentState.categoryId == null) {
            categoryError = "Выберите категорию"
            hasErrors = true
        }

        if (hasErrors) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    skuError = skuError,
                    priceError = priceError,
                    stockError = stockError,
                    categoryError = categoryError
                )
            }
            return
        }

        // Создание товара
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    generalError = null
                )
            }

            val productDto = ProductCreateDTO(
                name = currentState.name.trim(),
                description = currentState.description.trim().ifBlank { null },
                price = price!!,
                stockQuantity = stock!!,
                categoryId = currentState.categoryId!!,
                sku = currentState.sku.trim()
            )

            when (val result = productRepository.createProduct(productDto)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.message ?: "Ошибка при создании товара"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}