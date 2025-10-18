package com.example.ecommerceapp.data.model

data class ProductReviewDTO(
    val id: Int,
    val productId: Int,
    val userId: Int,
    val username: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String,
    val hasImage: Boolean
)

data class ProductReviewCreateDTO(
    val rating: Int,
    val comment: String?
)

data class ProductReviewUpdateDTO(
    val rating: Int?,
    val comment: String?
)
