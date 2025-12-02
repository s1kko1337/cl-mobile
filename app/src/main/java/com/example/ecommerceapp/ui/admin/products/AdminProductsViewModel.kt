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

/**
 * Состояние экрана управления товарами для администратора.
 *
 * @property products Список всех товаров в системе
 * @property isLoading Индикатор загрузки списка товаров
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property showAddDialog Флаг отображения диалога создания нового товара
 */
data class AdminProductsState(
    val products: List<ProductDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false
)

/**
 * ViewModel для экрана управления товарами администратором.
 *
 * Управляет списком товаров, их созданием и удалением.
 *
 * @property productRepository Репозиторий для работы с товарами
 * @property categoryRepository Репозиторий для работы с категориями
 */
@HiltViewModel
class AdminProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProductsState())

    /**
     * Реактивный поток состояния экрана управления товарами.
     */
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Загружает список всех товаров с сервера.
     */
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

    /**
     * Открывает диалог создания нового товара.
     */
    fun showAddDialog() {
        _state.update { it.copy(showAddDialog = true) }
    }

    /**
     * Закрывает диалог создания нового товара.
     */
    fun hideAddDialog() {
        _state.update { it.copy(showAddDialog = false) }
    }

    /**
     * Создаёт новый товар в системе.
     *
     * После успешного создания обновляет список товаров и закрывает диалог.
     *
     * @param name Название товара
     * @param description Описание товара
     * @param price Цена товара
     * @param stock Количество на складе
     * @param categoryId ID категории товара
     * @param sku Артикул товара
     */
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

    /**
     * Удаляет товар по его ID.
     *
     * После успешного удаления обновляет список товаров.
     *
     * @param id ID товара для удаления
     */
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

    /**
     * Обновляет список товаров.
     */
    fun refresh() {
        loadProducts()
    }
}