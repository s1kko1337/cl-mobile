package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import javax.inject.Inject

/**
 * Репозиторий для работы с заказами.
 *
 * Предоставляет методы для получения, создания, обновления и удаления заказов.
 *
 * @property api Сервис API для выполнения сетевых запросов
 */
class OrderRepository @Inject constructor(
    private val api: ApiService
) {
    /**
     * Получает список всех заказов текущего пользователя.
     *
     * @return Resource со списком OrderDTO или сообщением об ошибке
     */
    suspend fun getOrders(): Resource<List<OrderDTO>> {
        return try {
            val response = api.getOrders()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Получает детальную информацию о заказе по ID.
     *
     * @param id ID заказа
     * @return Resource с OrderDTO или сообщением об ошибке
     */
    suspend fun getOrder(id: Int): Resource<OrderDTO> {
        return try {
            val response = api.getOrder(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Создает новый заказ.
     *
     * @param order Данные для создания заказа
     * @return Resource с созданным OrderDTO или сообщением об ошибке
     */
    suspend fun createOrder(order: OrderCreateDTO): Resource<OrderDTO> {
        return try {
            val response = api.createOrder(order)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error(errorBody ?: response.message() ?: "Failed to create order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Обновляет статус заказа.
     *
     * @param id ID заказа для обновления
     * @param order Данные для обновления статуса заказа
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun updateOrderStatus(id: Int, order: OrderUpdateDTO): Resource<Unit> {
        return try {
            val response = api.updateOrder(id, order)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to update order status")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Удаляет заказ.
     *
     * @param id ID заказа для удаления
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun deleteOrder(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteOrder(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}