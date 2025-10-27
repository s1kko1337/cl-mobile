package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val api: ApiService
) {
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