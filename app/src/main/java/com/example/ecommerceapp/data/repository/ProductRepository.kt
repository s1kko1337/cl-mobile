package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Репозиторий для работы с продуктами.
 *
 * Предоставляет методы для получения, создания, обновления и удаления продуктов,
 * а также для работы с изображениями продуктов.
 *
 * @property api Сервис API для выполнения сетевых запросов
 */
class ProductRepository @Inject constructor(
    private val api: ApiService
) {
    /**
     * Получает список всех продуктов.
     *
     * @return Resource с списком ProductDTO или сообщением об ошибке
     */
    suspend fun getProducts(): Resource<List<ProductDTO>> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch products")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Загружает изображение для продукта.
     *
     * @param productId ID продукта
     * @param imageFile Файл изображения для загрузки
     * @param altText Альтернативный текст для изображения (опционально)
     * @return Resource с ProductImageDTO или сообщением об ошибке
     */
    suspend fun uploadProductImage(
        productId: Int,
        imageFile: File,
        altText: String? = null
    ): Resource<ProductImageDTO> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            val altTextBody = altText?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.uploadProductImage(productId, body, altTextBody)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to upload image")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Получает информацию о всех изображениях продукта.
     *
     * @param productId ID продукта
     * @return Resource со списком ProductImageDTO или сообщением об ошибке
     */
    suspend fun getProductImages(productId: Int): Resource<List<ProductImageDTO>> {
        return try {
            val response = api.getProductImagesInfo(productId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch images")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Удаляет изображение продукта.
     *
     * @param productId ID продукта
     * @param imageId ID изображения для удаления
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun deleteProductImage(productId: Int, imageId: Int): Resource<Unit> {
        return try {
            val response = api.deleteProductImage(productId, imageId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete image")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Получает детальную информацию о продукте по ID.
     *
     * @param id ID продукта
     * @return Resource с ProductDTO или сообщением об ошибке
     */
    suspend fun getProduct(id: Int): Resource<ProductDTO> {
        return try {
            val response = api.getProduct(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Создает новый продукт.
     *
     * @param product Данные для создания продукта
     * @return Resource с созданным ProductDTO или сообщением об ошибке
     */
    suspend fun createProduct(product: ProductCreateDTO): Resource<ProductDTO> {
        return try {
            val response = api.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Обновляет существующий продукт.
     *
     * @param id ID продукта для обновления
     * @param product Данные для обновления продукта
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun updateProduct(id: Int, product: ProductUpdateDTO): Resource<Unit> {
        return try {
            val response = api.updateProduct(id, product)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to update product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Удаляет продукт.
     *
     * @param id ID продукта для удаления
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun deleteProduct(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}