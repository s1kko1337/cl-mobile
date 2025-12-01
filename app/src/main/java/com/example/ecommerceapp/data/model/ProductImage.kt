package com.example.ecommerceapp.data.model

/**
 * DTO для представления изображения продукта из API.
 *
 * @property id Уникальный идентификатор изображения
 * @property productId ID продукта, к которому относится изображение
 * @property imageUrl URL изображения
 * @property altText Альтернативный текст для изображения (опционально)
 * @property displayOrder Порядок отображения изображения в списке
 */
data class ProductImageDTO(
    val id: Int,
    val productId: Int,
    val imageUrl: String,
    val altText: String?,
    val displayOrder: Int
)

/**
 * DTO для обновления метаданных изображения продукта.
 *
 * @property altText Новый альтернативный текст (опционально)
 * @property displayOrder Новый порядок отображения (опционально)
 */
data class ProductImageUpdateDTO(
    val altText: String?,
    val displayOrder: Int?
)
