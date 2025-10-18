package com.example.ecommerceapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val stock: Int
)