package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getCategories(): Resource<List<CategoryDTO>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch categories")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getCategoryProducts(id: Int): Resource<List<ProductDTO>> {
        return try {
            val response = api.getCategoryProducts(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch products")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
