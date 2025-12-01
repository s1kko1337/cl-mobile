package com.example.ecommerceapp.data.model

/**
 * DTO для представления отзыва на продукт из API.
 *
 * @property id Уникальный идентификатор отзыва
 * @property authorId ID автора отзыва
 * @property authorName Имя автора отзыва
 * @property rating Оценка продукта (обычно от 1 до 5)
 * @property comment Текст отзыва
 * @property createdAt Дата и время создания отзыва
 * @property updatedAt Дата и время последнего обновления отзыва (опционально)
 * @property reviewImageUrl URL изображения к отзыву (опционально)
 */
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

/**
 * DTO для создания нового отзыва на продукт.
 *
 * @property authorId ID автора отзыва
 * @property authorName Имя автора отзыва
 * @property rating Оценка продукта (обычно от 1 до 5)
 * @property comment Текст отзыва
 */
data class ProductReviewCreateDTO(
    val authorId: Int,
    val authorName: String,
    val rating: Int,
    val comment: String
)

/**
 * DTO для обновления существующего отзыва.
 *
 * @property authorName Новое имя автора
 * @property rating Новая оценка продукта
 * @property comment Новый текст отзыва
 */
data class ProductReviewUpdateDTO(
    val authorName: String,
    val rating: Int,
    val comment: String
)
