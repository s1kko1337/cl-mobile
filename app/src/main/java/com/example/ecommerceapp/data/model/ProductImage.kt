package com.example.ecommerceapp.data.model

data class ProductImageDTO(
    val id: Int,
    val productId: Int,
    val imageUrl: String,
    val altText: String?,
    val displayOrder: Int
)

data class ProductImageUpdateDTO(
    val altText: String?,
    val displayOrder: Int?
)
