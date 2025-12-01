package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import javax.inject.Inject

/**
 * Репозиторий для работы с категориями товаров.
 *
 * Предоставляет методы для получения, создания, обновления и удаления категорий,
 * а также для получения товаров в конкретной категории.
 *
 * @property api Сервис API для выполнения сетевых запросов
 */
class CategoryRepository @Inject constructor(
    private val api: ApiService
) {
    /**
     * Получает список всех категорий.
     *
     * @return Resource со списком CategoryDTO или сообщением об ошибке
     */
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

    /**
     * Получает список товаров в конкретной категории.
     *
     * @param id ID категории
     * @return Resource со списком ProductDTO или сообщением об ошибке
     */
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

    /**
     * Создает новую категорию.
     *
     * @param category Данные для создания категории
     * @return Resource с созданной CategoryDTO или сообщением об ошибке
     */
    suspend fun createCategory(category: CategoryCreateDTO): Resource<CategoryDTO> {
        return try {
            val response = api.createCategory(category)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Обновляет существующую категорию.
     *
     * @param id ID категории для обновления
     * @param category Данные для обновления категории
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun updateCategory(id: Int, category: CategoryUpdateDTO): Resource<Unit> {
        return try {
            val response = api.updateCategory(id, category)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to update category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Удаляет категорию.
     *
     * @param id ID категории для удаления
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun deleteCategory(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteCategory(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
