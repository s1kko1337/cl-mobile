package com.example.ecommerceapp.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val api: ApiService
) {
    private val imageCache = mutableMapOf<String, Bitmap>()

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

    fun clearCache() {
        imageCache.clear()
    }

    fun removeCachedImage(cacheKey: String) {
        imageCache.remove(cacheKey)
    }
}