package com.example.ecommerceapp.ui.customer.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CartItem
import com.example.ecommerceapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val isProcessing: Boolean = false,
    val orderCompleted: Boolean = false
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _state.update {
                    it.copy(
                        items = items,
                        total = items.sumOf { item -> item.price * item.quantity }
                    )
                }
            }
        }
    }

    fun placeOrder(
        name: String,
        address: String,
        phone: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }

            // Simulate order processing
            kotlinx.coroutines.delay(2000)

            // Clear cart
            cartRepository.clearCart()

            _state.update {
                it.copy(
                    isProcessing = false,
                    orderCompleted = true
                )
            }
        }
    }
}