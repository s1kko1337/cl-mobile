package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.CartDao
import com.example.ecommerceapp.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    fun getCartItems(): Flow<List<CartItem>> = cartDao.getCartItems()

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()

    suspend fun addToCart(item: CartItem) {
        val existingItem = cartDao.getCartItem(item.productId)
        if (existingItem != null) {
            cartDao.updateCartItem(existingItem.copy(quantity = existingItem.quantity + item.quantity))
        } else {
            cartDao.insertCartItem(item)
        }
    }

    suspend fun updateCartItem(item: CartItem) {
        cartDao.updateCartItem(item)
    }

    suspend fun removeFromCart(item: CartItem) {
        cartDao.deleteCartItem(item)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
