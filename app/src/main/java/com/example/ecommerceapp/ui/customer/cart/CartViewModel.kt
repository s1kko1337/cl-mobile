package com.example.ecommerceapp.ui.customer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CartItem
import com.example.ecommerceapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val state: StateFlow<CartState> = cartRepository.getCartItems()
        .map { items ->
            CartState(
                items = items,
                total = items.sumOf { it.price * it.quantity }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartState()
        )

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                cartRepository.removeFromCart(item)
            } else if (newQuantity <= item.stock) {
                cartRepository.updateCartItem(item.copy(quantity = newQuantity))
            }
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}