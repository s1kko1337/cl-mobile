package com.example.ecommerceapp.data.model

data class CategoryDTO(
    val id: Int,
    val name: String,
    val description: String?,
)

data class CategoryCreateDTO(
    val name: String,
    val description: String?,
)

data class CategoryUpdateDTO(
    val name: String?,
    val description: String?,
)
