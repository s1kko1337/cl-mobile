package com.example.ecommerceapp.data.model

data class ProductDTO(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stockQuantity: Int,
    val categoryId: Int,
    val images: List<ProductImageDTO>? = null,
    val averageRating: Double? = null,
    val reviewsCount: Int? = null,
    val sku: String
)

data class ProductCreateDTO(
    val name: String,
    val description: String?,
    val price: Double,
    val stockQuantity: Int,
    val categoryId: Int,
    val sku: String
)

data class ProductUpdateDTO(
    val name: String?,
    val description: String?,
    val price: Double?,
    val stockQuantity: Int?,
    val categoryId: Int?,
    val sku: String
)
