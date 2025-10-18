package com.example.ecommerceapp.data.model

data class ProductDTO(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val categoryId: Int,
    val images: List<ProductImageDTO>? = null,
    val averageRating: Double? = null,
    val reviewsCount: Int? = null
)

data class ProductCreateDTO(
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val categoryId: Int
)

data class ProductUpdateDTO(
    val name: String?,
    val description: String?,
    val price: Double?,
    val stock: Int?,
    val categoryId: Int?
)