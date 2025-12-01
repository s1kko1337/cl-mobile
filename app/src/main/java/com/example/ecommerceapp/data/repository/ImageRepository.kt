package com.example.ecommerceapp.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с изображениями.
 *
 * Загружает изображения продуктов и отзывов с сервера и кэширует их в памяти.
 * Является синглтоном для сохранения кэша между использованиями.
 *
 * @property api Сервис API для выполнения сетевых запросов
 */
@Singleton
class ImageRepository @Inject constructor(
    private val api: ApiService
) {
    private val imageCache = mutableMapOf<String, Bitmap>()

    /**
     * Получает изображение продукта по ID.
     *
     * Сначала проверяет кэш, если изображение не найдено - загружает с сервера.
     *
     * @param productId ID продукта
     * @param imageId ID изображения
     * @return Resource с Bitmap или сообщением об ошибке
     */
    suspend fun getProductImage(productId: Int, imageId: Int): Resource<Bitmap> {
        val cacheKey = "product_${productId}_$imageId"

        imageCache[cacheKey]?.let {
            return Resource.Success(it)
        }

        return try {
            withContext(Dispatchers.IO) {
                val response = api.downloadProductImage(productId, imageId)
                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        imageCache[cacheKey] = bitmap
                        Resource.Success(bitmap)
                    } else {
                        Resource.Error("Failed to decode image")
                    }
                } else {
                    Resource.Error(response.message() ?: "Failed to download image")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Получает изображение отзыва по ID.
     *
     * Сначала проверяет кэш, если изображение не найдено - загружает с сервера.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @return Resource с Bitmap или сообщением об ошибке
     */
    suspend fun getReviewImage(productId: Int, reviewId: Int): Resource<Bitmap> {
        val cacheKey = "review_${productId}_$reviewId"

        imageCache[cacheKey]?.let {
            return Resource.Success(it)
        }

        return try {
            withContext(Dispatchers.IO) {
                val response = api.downloadReviewImage(productId, reviewId)
                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        imageCache[cacheKey] = bitmap
                        Resource.Success(bitmap)
                    } else {
                        Resource.Error("Failed to decode image")
                    }
                } else {
                    Resource.Error(response.message() ?: "Failed to download image")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Очищает весь кэш изображений.
     */
    fun clearCache() {
        imageCache.clear()
    }

    /**
     * Удаляет конкретное изображение из кэша.
     *
     * @param cacheKey Ключ изображения в кэше
     */
    fun removeCachedImage(cacheKey: String) {
        imageCache.remove(cacheKey)
    }
}