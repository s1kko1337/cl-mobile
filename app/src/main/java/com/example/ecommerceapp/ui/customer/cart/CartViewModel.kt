package com.example.ecommerceapp.ui.customer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CartItem
import com.example.ecommerceapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана корзины покупок.
 *
 * @property items Список товаров в корзине
 * @property total Общая стоимость всех товаров в корзине
 */
data class CartState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

/**
 * ViewModel для экрана корзины покупок.
 *
 * Управляет отображением товаров в корзине, изменением количества товаров,
 * удалением товаров и расчётом общей стоимости. Состояние обновляется
 * реактивно при любых изменениях в локальной базе данных.
 *
 * @property cartRepository Репозиторий для работы с корзиной
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    /**
     * Реактивный поток состояния корзины.
     *
     * Автоматически пересчитывает общую стоимость при изменении товаров в корзине.
     * Использует стратегию WhileSubscribed для оптимизации ресурсов.
     */
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

    /**
     * Обновляет количество товара в корзине.
     *
     * Если новое количество <= 0, товар удаляется из корзины.
     * Если новое количество превышает остаток на складе, изменение игнорируется.
     *
     * @param item Товар для обновления
     * @param newQuantity Новое количество товара
     */
    fun updateQuantity(item: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                cartRepository.removeFromCart(item)
            } else if (newQuantity <= item.stock) {
                cartRepository.updateCartItem(item.copy(quantity = newQuantity))
            }
        }
    }

    /**
     * Удаляет товар из корзины.
     *
     * @param item Товар для удаления
     */
    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item)
        }
    }

    /**
     * Очищает всю корзину, удаляя все товары.
     */
    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}