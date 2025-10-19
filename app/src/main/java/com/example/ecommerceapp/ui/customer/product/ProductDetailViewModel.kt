package com.example.ecommerceapp.ui.customer.product


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ProductDetailState(
    val product: ProductDTO? = null,
    val reviews: List<ProductReviewDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val addedToCart: Boolean = false
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = savedStateHandle.get<Int>("productId") ?: 0

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    init {
        loadProduct()
        loadReviews()
    }

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

    fun addToCart(quantity: Int) {
        viewModelScope.launch {
            val product = _state.value.product ?: return@launch
            val cartItem = CartItem(
                productId = product.id,
                name = product.name,
                price = product.price,
                quantity = quantity,
                imageUrl = product.images?.firstOrNull()?.imageUrl,
                stock = product.stock
            )
            cartRepository.addToCart(cartItem)
            _state.update { it.copy(addedToCart = true) }
        }
    }

    fun resetAddedToCart() {
        _state.update { it.copy(addedToCart = false) }
    }
}