package com.example.ecommerceapp.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.OrderDTO
import com.example.ecommerceapp.data.model.ProductReviewCreateDTO
import com.example.ecommerceapp.data.model.ProductReviewDTO
import com.example.ecommerceapp.data.model.ProductReviewUpdateDTO
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.data.repository.ReviewRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Состояние экрана детальной информации о заказе.
 *
 * @property order Информация о заказе
 * @property isLoading Индикатор загрузки данных
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property orderDeleted Флаг успешного удаления заказа
 * @property productReviews Карта отзывов пользователя на товары (productId -> отзыв или null)
 * @property reviewSubmitted Флаг успешной отправки/обновления/удаления отзыва
 */
data class OrderDetailState(
    val order: OrderDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderDeleted: Boolean = false,
    val productReviews: Map<Int, ProductReviewDTO?> = emptyMap(),
    val reviewSubmitted: Boolean = false
)

/**
 * ViewModel для экрана детальной информации о заказе.
 *
 * Управляет загрузкой данных о конкретном заказе, работой с отзывами на товары
 * из заказа (создание, обновление, удаление) и удалением заказа.
 *
 * @property orderRepository Репозиторий для работы с заказами
 * @property reviewRepository Репозиторий для работы с отзывами
 * @property userPreferences Менеджер пользовательских настроек для получения ID и имени пользователя
 */
@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val reviewRepository: ReviewRepository,
    private val userPreferences: com.example.ecommerceapp.data.local.UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailState())

    /**
     * Реактивный поток состояния экрана детальной информации о заказе.
     */
    val state = _state.asStateFlow()

    private var currentUserId: Int = 0
    private var currentUsername: String = ""

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { id ->
                currentUserId = id ?: 0
            }
        }
        viewModelScope.launch {
            userPreferences.username.collect { name ->
                currentUsername = name ?: ""
            }
        }
    }

    /**
     * Загружает детальную информацию о заказе по его ID.
     *
     * Если заказ имеет статус "Completed", дополнительно загружает
     * отзывы пользователя на товары из этого заказа.
     *
     * @param orderId ID заказа для загрузки
     */
    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.getOrder(orderId)) {
                is Resource.Success -> {
                    val order = result.data
                    _state.update {
                        it.copy(
                            order = order,
                            isLoading = false
                        )
                    }

                    if (order?.status == "Completed") {
                        loadProductReviews(order.orderItems.map { item -> item.productId })
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
                is Resource.Loading<*> -> {}
            }
        }
    }

    /**
     * Загружает отзывы текущего пользователя на указанные товары.
     *
     * Для каждого товара ищет отзыв от текущего пользователя и сохраняет результат в состояние.
     *
     * @param productIds Список ID товаров для загрузки отзывов
     */
    private fun loadProductReviews(productIds: List<Int>) {
        viewModelScope.launch {
            val reviewsMap = mutableMapOf<Int, ProductReviewDTO?>()

            productIds.forEach { productId ->
                when (val result = reviewRepository.getProductReviews(productId)) {
                    is Resource.Success -> {
                        reviewsMap[productId] = result.data?.find { review ->
                            review.authorId == currentUserId
                        }
                    }
                    is Resource.Error -> {
                        reviewsMap[productId] = null
                    }
                    is Resource.Loading -> {}
                }
            }

            _state.update { it.copy(productReviews = reviewsMap) }
        }
    }

    /**
     * Создаёт новый отзыв на товар.
     *
     * @param productId ID товара
     * @param rating Оценка товара (обычно от 1 до 5)
     * @param comment Текст комментария
     */
    fun submitReview(productId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val reviewDto = ProductReviewCreateDTO(
                authorId = currentUserId,
                authorName = currentUsername,
                rating = rating,
                comment = comment.ifBlank { "Без комментария" }
            )

            when (val result = reviewRepository.createReview(productId, reviewDto)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            reviewSubmitted = true
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
     * Обновляет существующий отзыв на товар.
     *
     * Позволяет обновить текст и оценку, загрузить новое изображение
     * или удалить существующее.
     *
     * @param productId ID товара
     * @param reviewId ID отзыва
     * @param rating Новая оценка товара
     * @param comment Новый текст комментария
     * @param imageFile Файл изображения для загрузки (необязательно)
     * @param deleteImage Удалить ли существующее изображение
     */
    fun updateReview(
        productId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        imageFile: File? = null,
        deleteImage: Boolean = false
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val reviewDto = ProductReviewUpdateDTO(
                authorName = currentUsername,
                rating = rating,
                comment = comment.ifBlank { "Без комментария" }
            )

            when (val result = reviewRepository.updateReview(productId, reviewId, reviewDto)) {
                is Resource.Success -> {
                    var imageError: String? = null

                    if (deleteImage) {
                        when (reviewRepository.deleteReviewImage(productId, reviewId)) {
                            is Resource.Error -> {
                                imageError = "Не удалось удалить изображение"
                            }
                            else -> {}
                        }
                    }

                    if (imageFile != null) {
                        when (reviewRepository.uploadReviewImage(productId, reviewId, imageFile)) {
                            is Resource.Error -> {
                                imageError = "Не удалось загрузить изображение"
                            }
                            else -> {}
                        }
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            reviewSubmitted = true,
                            error = imageError
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
     * Удаляет отзыв на товар.
     *
     * @param productId ID товара
     * @param reviewId ID отзыва для удаления
     */
    fun deleteReview(productId: Int, reviewId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = reviewRepository.deleteReview(productId, reviewId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            reviewSubmitted = true
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
     * Удаляет заказ по его ID.
     *
     * @param orderId ID заказа для удаления
     */
    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.deleteOrder(orderId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            orderDeleted = true
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
                is Resource.Loading<*> -> {}
            }
        }
    }

    /**
     * Сбрасывает флаг успешной отправки отзыва в состоянии.
     */
    fun resetReviewSubmitted() {
        _state.update { it.copy(reviewSubmitted = false) }
    }
}
