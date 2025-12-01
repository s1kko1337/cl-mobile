package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.CartDao
import com.example.ecommerceapp.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Репозиторий для работы с корзиной покупок.
 *
 * Управляет элементами корзины в локальной базе данных Room.
 * Предоставляет методы для добавления, обновления, удаления товаров из корзины.
 *
 * @property cartDao DAO для работы с таблицей корзины
 */
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    /**
     * Получает Flow со списком всех элементов в корзине.
     *
     * @return Flow со списком CartItem
     */
    fun getCartItems(): Flow<List<CartItem>> = cartDao.getCartItems()

    /**
     * Получает Flow с количеством элементов в корзине.
     *
     * @return Flow с количеством элементов
     */
    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()

    /**
     * Добавляет товар в корзину.
     *
     * Если товар уже есть в корзине, увеличивает его количество.
     * Если товара нет, добавляет новый элемент.
     *
     * @param item Элемент корзины для добавления
     */
    suspend fun addToCart(item: CartItem) {
        val existingItem = cartDao.getCartItem(item.productId)
        if (existingItem != null) {
            cartDao.updateCartItem(existingItem.copy(quantity = existingItem.quantity + item.quantity))
        } else {
            cartDao.insertCartItem(item)
        }
    }

    /**
     * Обновляет элемент корзины.
     *
     * @param item Элемент корзины с обновленными данными
     */
    suspend fun updateCartItem(item: CartItem) {
        cartDao.updateCartItem(item)
    }

    /**
     * Удаляет товар из корзины.
     *
     * @param item Элемент корзины для удаления
     */
    suspend fun removeFromCart(item: CartItem) {
        cartDao.deleteCartItem(item)
    }

    /**
     * Очищает всю корзину.
     *
     * Удаляет все элементы из корзины покупок.
     */
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
