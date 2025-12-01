package com.example.ecommerceapp.data.model

/**
 * DTO для представления продукта из API.
 *
 * @property id Уникальный идентификатор продукта
 * @property name Название продукта
 * @property description Описание продукта (опционально)
 * @property price Цена продукта
 * @property stockQuantity Количество товара на складе
 * @property categoryId ID категории, к которой относится продукт
 * @property images Список изображений продукта (опционально)
 * @property averageRating Средний рейтинг продукта на основе отзывов (опционально)
 * @property reviewsCount Количество отзывов на продукт (опционально)
 * @property sku Артикул товара (Stock Keeping Unit)
 */
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

/**
 * DTO для создания нового продукта.
 *
 * @property name Название продукта
 * @property description Описание продукта (опционально)
 * @property price Цена продукта
 * @property stockQuantity Начальное количество товара на складе
 * @property categoryId ID категории, к которой относится продукт
 * @property sku Артикул товара (Stock Keeping Unit)
 */
data class ProductCreateDTO(
    val name: String,
    val description: String?,
    val price: Double,
    val stockQuantity: Int,
    val categoryId: Int,
    val sku: String
)

/**
 * DTO для обновления существующего продукта.
 *
 * Все поля опциональны, обновляются только переданные значения.
 *
 * @property name Новое название продукта (опционально)
 * @property description Новое описание продукта (опционально)
 * @property price Новая цена продукта (опционально)
 * @property stockQuantity Новое количество товара на складе (опционально)
 * @property categoryId Новый ID категории (опционально)
 * @property sku Артикул товара
 */
data class ProductUpdateDTO(
    val name: String?,
    val description: String?,
    val price: Double?,
    val stockQuantity: Int?,
    val categoryId: Int?,
    val sku: String
)
