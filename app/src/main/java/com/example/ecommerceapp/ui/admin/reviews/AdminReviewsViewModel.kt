package com.example.ecommerceapp.ui.admin.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.ProductDTO
import com.example.ecommerceapp.data.model.ProductReviewDTO
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.data.repository.ReviewRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewWithProduct(
    val review: ProductReviewDTO,
    val productName: String,
    val productId: Int
)

data class AdminReviewsState(
    val reviews: List<ReviewWithProduct> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedProductId: Int? = null,
    val products: List<ProductDTO> = emptyList()
)

@HiltViewModel
class AdminReviewsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminReviewsState())
    val state = _state.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load products
            when (val productsResult = productRepository.getProducts()) {
                is Resource.Success -> {
                    val products = productsResult.data ?: emptyList()
                    _state.update { it.copy(products = products) }

                    // Load all reviews from all products
                    val allReviews = mutableListOf<ReviewWithProduct>()
                    products.forEach { product ->
                        when (val reviewsResult = reviewRepository.getProductReviews(product.id)) {
                            is Resource.Success -> {
                                reviewsResult.data?.forEach { review ->
                                    allReviews.add(
                                        ReviewWithProduct(
                                            review = review,
                                            productName = product.name,
                                            productId = product.id
                                        )
                                    )
                                }
                            }
                            is Resource.Error -> {
                                // Игнорируем ошибки для отдельных продуктов
                            }

                            is Resource.Loading<*> -> TODO()
                        }
                    }

                    _state.update {
                        it.copy(
                            reviews = allReviews.sortedByDescending { r -> r.review.createdAt },
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = productsResult.message
                        )
                    }
                }

                is Resource.Loading<*> -> TODO()
            }
        }
    }

    fun filterByProduct(productId: Int?) {
        viewModelScope.launch {
            _state.update { it.copy(selectedProductId = productId, isLoading = true) }

            if (productId == null) {
                loadData()
            } else {
                when (val result = reviewRepository.getProductReviews(productId)) {
                    is Resource.Success -> {
                        val product = _state.value.products.find { it.id == productId }
                        val reviews = result.data?.map { review ->
                            ReviewWithProduct(
                                review = review,
                                productName = product?.name ?: "Unknown",
                                productId = productId
                            )
                        } ?: emptyList()

                        _state.update {
                            it.copy(
                                reviews = reviews,
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

                    is Resource.Loading<*> -> TODO()
                }
            }
        }
    }

    fun deleteReview(productId: Int, reviewId: Int) {
        viewModelScope.launch {
            when (reviewRepository.deleteReview(productId, reviewId)) {
                is Resource.Success -> {
                    if (_state.value.selectedProductId != null) {
                        filterByProduct(_state.value.selectedProductId)
                    } else {
                        loadData()
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = "Ошибка удаления отзыва") }
                }

                is Resource.Loading<*> -> TODO()
            }
        }
    }

    fun refresh() {
        if (_state.value.selectedProductId != null) {
            filterByProduct(_state.value.selectedProductId)
        } else {
            loadData()
        }
    }
}
