package com.example.ecommerceapp.data.model

/**
 * DTO для представления категории товаров из API.
 *
 * @property id Уникальный идентификатор категории
 * @property name Название категории
 * @property description Описание категории (опционально)
 */
data class CategoryDTO(
    val id: Int,
    val name: String,
    val description: String?,
)

/**
 * DTO для создания новой категории.
 *
 * @property name Название категории
 * @property description Описание категории (опционально)
 */
data class CategoryCreateDTO(
    val name: String,
    val description: String?,
)

/**
 * DTO для обновления существующей категории.
 *
 * Все поля опциональны, обновляются только переданные значения.
 *
 * @property name Новое название категории (опционально)
 * @property description Новое описание категории (опционально)
 */
data class CategoryUpdateDTO(
    val name: String?,
    val description: String?,
)
