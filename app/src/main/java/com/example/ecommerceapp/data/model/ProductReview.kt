package com.example.ecommerceapp.data.model

data class ProductReviewDTO(
    val id: Int,
    val authorId: Int,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String,
    val updatedAt: String?,
    val reviewImageUrl: String?
)

data class ProductReviewCreateDTO(
    val authorId: Int,
    val authorName: String,
    val rating: Int,
    val comment: String
)

data class ProductReviewUpdateDTO(
    val authorName: String,
    val rating: Int,
    val comment: String
)
