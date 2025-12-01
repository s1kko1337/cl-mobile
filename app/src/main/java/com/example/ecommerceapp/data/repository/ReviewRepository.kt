package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Репозиторий для работы с отзывами на продукты.
 *
 * Предоставляет методы для получения, создания, обновления и удаления отзывов,
 * а также для работы с изображениями отзывов.
 *
 * @property api Сервис API для выполнения сетевых запросов
 */
class ReviewRepository @Inject constructor(
    private val api: ApiService
) {
    /**
     * Получает все отзывы на продукт.
     *
     * @param productId ID продукта
     * @return Resource со списком ProductReviewDTO или сообщением об ошибке
     */
    suspend fun getProductReviews(productId: Int): Resource<List<ProductReviewDTO>> {
        return try {
            val response = api.getProductReviews(productId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch reviews")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Создает новый отзыв на продукт.
     *
     * @param productId ID продукта
     * @param review Данные для создания отзыва
     * @return Resource с созданным ProductReviewDTO или сообщением об ошибке
     */
    suspend fun createReview(productId: Int, review: ProductReviewCreateDTO): Resource<ProductReviewDTO> {
        return try {
            val response = api.createReview(productId, review)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create review")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва для обновления
     * @param review Данные для обновления отзыва
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun updateReview(
        productId: Int,
        reviewId: Int,
        review: ProductReviewUpdateDTO
    ): Resource<Unit> {
        return try {
            val response = api.updateReview(productId, reviewId, review)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to update review")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Удаляет отзыв.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва для удаления
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun deleteReview(productId: Int, reviewId: Int): Resource<Unit> {
        return try {
            val response = api.deleteReview(productId, reviewId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete review")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Загружает изображение для отзыва.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @param imageFile Файл изображения для загрузки
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun uploadReviewImage(
        productId: Int,
        reviewId: Int,
        imageFile: File
    ): Resource<Unit> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = api.uploadReviewImage(productId, reviewId, body)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to upload image")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Удаляет изображение отзыва.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun deleteReviewImage(productId: Int, reviewId: Int): Resource<Unit> {
        return try {
            val response = api.deleteReviewImage(productId, reviewId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete review image")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}