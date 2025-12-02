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

/**
 * Состояние диалога создания нового товара.
 *
 * @property name Название товара
 * @property sku Артикул товара
 * @property description Описание товара
 * @property price Цена в виде строки
 * @property stockQuantity Количество на складе в виде строки
 * @property categoryId ID выбранной категории
 * @property categories Список доступных категорий
 * @property isLoading Индикатор процесса создания товара
 * @property nameError Ошибка поля названия
 * @property skuError Ошибка поля артикула
 * @property priceError Ошибка поля цены
 * @property stockError Ошибка поля количества
 * @property categoryError Ошибка выбора категории
 * @property generalError Общая ошибка операции
 * @property success Флаг успешного создания товара
 */
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

/**
 * ViewModel для диалога создания нового товара.
 *
 * Управляет вводом данных нового товара, валидацией полей и созданием товара.
 *
 * @property productRepository Репозиторий для работы с товарами
 * @property categoryRepository Репозиторий для работы с категориями
 */
@HiltViewModel
class AddProductDialogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductDialogState())

    /**
     * Реактивный поток состояния диалога создания товара.
     */
    val state: StateFlow<AddProductDialogState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Загружает список категорий для выбора.
     */
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

    /** Обновляет название товара. @param value Новое название */
    fun onNameChange(value: String) {
        _state.update {
            it.copy(
                name = value,
                nameError = null
            )
        }
    }

    /** Обновляет артикул товара. @param value Новый артикул */
    fun onSkuChange(value: String) {
        _state.update {
            it.copy(
                sku = value,
                skuError = null
            )
        }
    }

    /** Обновляет описание товара. @param value Новое описание */
    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    /** Обновляет цену товара. @param value Новая цена */
    fun onPriceChange(value: String) {
        _state.update {
            it.copy(
                price = value,
                priceError = null
            )
        }
    }

    /** Обновляет количество товара. @param value Новое количество */
    fun onStockQuantityChange(value: String) {
        _state.update {
            it.copy(
                stockQuantity = value,
                stockError = null
            )
        }
    }

    /** Выбирает категорию товара. @param categoryId ID категории */
    fun onCategorySelected(categoryId: Int) {
        _state.update {
            it.copy(
                categoryId = categoryId,
                categoryError = null
            )
        }
    }

    /**
     * Создаёт новый товар после валидации всех полей.
     */
    fun createProduct() {
        val currentState = _state.value

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