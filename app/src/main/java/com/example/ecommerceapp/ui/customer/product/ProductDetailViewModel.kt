package com.example.ecommerceapp.ui.customer.product


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.BuildConfig
import com.example.ecommerceapp.data.model.CartItem
import com.example.ecommerceapp.data.model.ProductDTO
import com.example.ecommerceapp.data.model.ProductReviewDTO
import com.example.ecommerceapp.data.repository.CartRepository
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.data.repository.ReviewRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана детальной информации о продукте.
 *
 * @property product Информация о продукте
 * @property reviews Список отзывов на продукт
 * @property isLoading Индикатор загрузки данных
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property addedToCart Флаг успешного добавления товара в корзину
 */
data class ProductDetailState(
    val product: ProductDTO? = null,
    val reviews: List<ProductReviewDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val addedToCart: Boolean = false
)

/**
 * ViewModel для экрана детальной информации о продукте.
 *
 * Управляет загрузкой информации о продукте, отзывов и добавлением товара в корзину.
 * Получает ID продукта из параметров навигации через SavedStateHandle.
 *
 * @property productRepository Репозиторий для работы с продуктами
 * @property reviewRepository Репозиторий для работы с отзывами
 * @property cartRepository Репозиторий для работы с корзиной
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = savedStateHandle.get<Int>("productId") ?: 0

    private val _state = MutableStateFlow(ProductDetailState())

    /**
     * Реактивный поток состояния экрана продукта.
     */
    val state = _state.asStateFlow()

    init {
        loadProduct()
        loadReviews()
    }

    /**
     * Загружает информацию о продукте с сервера.
     */
    private fun loadProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = productRepository.getProduct(productId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            product = result.data,
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
     * Загружает отзывы на продукт с сервера.
     */
    private fun loadReviews() {
        viewModelScope.launch {
            when (val result = reviewRepository.getProductReviews(productId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(reviews = result.data ?: emptyList())
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Добавляет продукт в корзину с указанным количеством.
     *
     * Формирует полный URL изображения товара на основе базового URL API.
     * Создаёт CartItem и сохраняет его в локальную базу данных.
     *
     * @param quantity Количество товара для добавления
     */
    fun addToCart(quantity: Int) {
        viewModelScope.launch {
            val product = _state.value.product ?: return@launch

            val firstImage = product.images?.firstOrNull()

            val imageUrl = firstImage?.imageUrl?.let { relativeUrl ->
                if (relativeUrl.startsWith("http")) {
                    relativeUrl
                } else {
                    "${BuildConfig.BASE_URL}$relativeUrl"
                }
            }

            val cartItem = CartItem(
                productId = product.id,
                name = product.name,
                price = product.price,
                quantity = quantity,
                imageUrl = imageUrl,
                imageId = firstImage?.id,
                stock = product.stockQuantity
            )
            cartRepository.addToCart(cartItem)
            _state.update { it.copy(addedToCart = true) }
        }
    }

    /**
     * Сбрасывает флаг добавления товара в корзину.
     */
    fun resetAddedToCart() {
        _state.update { it.copy(addedToCart = false) }
    }
}