package com.example.ecommerceapp.ui.customer.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CartItem
import com.example.ecommerceapp.data.model.OrderCreateDTO
import com.example.ecommerceapp.data.model.OrderItemCreateDTO
import com.example.ecommerceapp.data.repository.CartRepository
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val isProcessing: Boolean = false,
    val orderCompleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
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
            _state.update { it.copy(isProcessing = true, error = null) }

            // Преобразуем paymentMethod в нужный формат
            val method = when (paymentMethod) {
                "card" -> "Card"
                "cash" -> "Cash"
                else -> "Card"
            }

            // Создаем список товаров для заказа
            val orderItems = _state.value.items.map { item ->
                OrderItemCreateDTO(
                    productId = item.productId,
                    quantity = item.quantity
                )
            }

            // Создаем заказ
            val orderCreateDTO = OrderCreateDTO(
                customerName = name,
                customerPhone = phone,
                deliveryAddress = address,
                paymentMethod = method,
                orderItems = orderItems
            )

            when (val result = orderRepository.createOrder(orderCreateDTO)) {
                is Resource.Success -> {
                    // Очищаем корзину после успешного создания заказа
                    cartRepository.clearCart()
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            orderCompleted = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            error = result.message
                        )
                    }
                }

                is Resource.Loading<*> -> TODO()
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}