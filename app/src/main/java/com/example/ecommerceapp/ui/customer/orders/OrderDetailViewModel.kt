package com.example.ecommerceapp.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.OrderDTO
import com.example.ecommerceapp.data.model.ProductReviewCreateDTO
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.data.repository.ReviewRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailState(
    val order: OrderDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderDeleted: Boolean = false,
    val productReviews: Map<Int, Boolean> = emptyMap(), // productId -> hasReview
    val reviewSubmitted: Boolean = false
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val reviewRepository: ReviewRepository,
    private val userPreferences: com.example.ecommerceapp.data.local.UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailState())
    val state = _state.asStateFlow()

    private var currentUserId: Int = 0
    private var currentUsername: String = ""

    init {
        // Load current user data
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

                    // Load reviews status for completed orders
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

    private fun loadProductReviews(productIds: List<Int>) {
        viewModelScope.launch {
            val reviewsMap = mutableMapOf<Int, Boolean>()

            productIds.forEach { productId ->
                when (val result = reviewRepository.getProductReviews(productId)) {
                    is Resource.Success -> {
                        reviewsMap[productId] = result.data?.any { review ->
                            review.userId == currentUserId
                        } ?: false
                    }
                    is Resource.Error -> {
                        reviewsMap[productId] = false
                    }
                    is Resource.Loading -> {}
                }
            }

            _state.update { it.copy(productReviews = reviewsMap) }
        }
    }

    fun submitReview(productId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val reviewDto = ProductReviewCreateDTO(
                userId = currentUserId,
                authorName = currentUsername,
                rating = rating,
                comment = comment.ifBlank { null }
            )

            when (val result = reviewRepository.createReview(productId, reviewDto)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            reviewSubmitted = true,
                            productReviews = it.productReviews + (productId to true)
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

    fun resetReviewSubmitted() {
        _state.update { it.copy(reviewSubmitted = false) }
    }
}