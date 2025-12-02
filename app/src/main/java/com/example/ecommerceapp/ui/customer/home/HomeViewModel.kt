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

/**
 * Состояние главного экрана приложения.
 *
 * @property products Список продуктов для отображения
 * @property categories Список категорий продуктов
 * @property isLoading Индикатор загрузки данных
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property selectedCategoryId ID выбранной категории для фильтрации (null - все категории)
 * @property cartItemCount Количество товаров в корзине
 */
data class HomeState(
    val products: List<ProductDTO> = emptyList(),
    val categories: List<CategoryDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategoryId: Int? = null,
    val cartItemCount: Int = 0
)

/**
 * ViewModel для главного экрана приложения.
 *
 * Управляет загрузкой и отображением списка продуктов и категорий,
 * фильтрацией по категориям и отслеживанием количества товаров в корзине.
 *
 * @property productRepository Репозиторий для работы с продуктами
 * @property categoryRepository Репозиторий для работы с категориями
 * @property cartRepository Репозиторий для работы с корзиной
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())

    /**
     * Реактивный поток состояния главного экрана.
     */
    val state = _state.asStateFlow()

    init {
        loadData()
        observeCartCount()
    }

    /**
     * Подписывается на изменения количества товаров в корзине.
     */
    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _state.update { it.copy(cartItemCount = count) }
            }
        }
    }

    /**
     * Загружает категории и продукты с сервера.
     */
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

    /**
     * Фильтрует продукты по выбранной категории.
     *
     * @param categoryId ID категории для фильтрации (null для сброса фильтра)
     */
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

    /**
     * Обновляет данные на экране (категории и продукты).
     */
    fun refresh() {
        loadData()
    }
}