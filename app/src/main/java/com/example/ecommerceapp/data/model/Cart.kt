package com.example.ecommerceapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность для хранения элемента корзины покупок в локальной БД.
 *
 * Используется Room для кэширования корзины покупателя между сессиями.
 *
 * @property productId Уникальный идентификатор продукта (первичный ключ)
 * @property name Название продукта
 * @property price Цена продукта
 * @property quantity Количество единиц продукта в корзине
 * @property imageUrl URL изображения продукта (опционально)
 * @property imageId ID изображения продукта (опционально)
 * @property stock Доступное количество товара на складе
 */
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val imageId: Int?,
    val stock: Int
)