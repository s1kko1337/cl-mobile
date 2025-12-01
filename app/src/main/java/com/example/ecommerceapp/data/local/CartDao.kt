package com.example.ecommerceapp.data.local

import androidx.room.*
import com.example.ecommerceapp.data.model.CartItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) для работы с корзиной в локальной базе данных Room.
 *
 * Предоставляет методы для добавления, обновления, удаления и получения товаров из корзины.
 * Использует Flow для реактивного отслеживания изменений данных.
 */
@Dao
interface CartDao {
    /**
     * Получает все товары в корзине в виде реактивного потока.
     *
     * @return Flow со списком CartItem, который автоматически обновляется при изменениях
     */
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    /**
     * Получает конкретный товар из корзины по ID продукта.
     *
     * @param productId ID продукта
     * @return CartItem или null, если товар не найден в корзине
     */
    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItem(productId: Int): CartItem?

    /**
     * Добавляет товар в корзину. При конфликте заменяет существующий товар.
     *
     * @param item Товар для добавления в корзину
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    /**
     * Обновляет существующий товар в корзине.
     *
     * @param item Товар с обновлёнными данными
     */
    @Update
    suspend fun updateCartItem(item: CartItem)

    /**
     * Удаляет товар из корзины.
     *
     * @param item Товар для удаления
     */
    @Delete
    suspend fun deleteCartItem(item: CartItem)

    /**
     * Очищает всю корзину, удаляя все товары.
     */
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    /**
     * Получает количество товаров в корзине в виде реактивного потока.
     *
     * @return Flow с количеством товаров в корзине
     */
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
}
