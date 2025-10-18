package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
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
}