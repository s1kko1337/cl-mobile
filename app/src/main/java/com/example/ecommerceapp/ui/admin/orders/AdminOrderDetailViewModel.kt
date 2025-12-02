package com.example.ecommerceapp.ui.admin.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.OrderDTO
import com.example.ecommerceapp.data.model.OrderUpdateDTO
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние детального просмотра заказа администратором.
 *
 * @property order Данные заказа
 * @property isLoading Индикатор загрузки или обновления данных
 * @property error Сообщение об ошибке
 * @property orderDeleted Флаг успешного удаления заказа
 * @property statusUpdated Флаг успешного обновления статуса заказа
 */
data class AdminOrderDetailState(
    val order: OrderDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderDeleted: Boolean = false,
    val statusUpdated: Boolean = false
)

/**
 * ViewModel для детального просмотра и управления заказом администратором.
 *
 * Управляет загрузкой заказа, обновлением его статуса и удалением.
 *
 * @property orderRepository Репозиторий для работы с заказами
 */
@HiltViewModel
class AdminOrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminOrderDetailState())

    /** Реактивный поток состояния детального просмотра заказа */
    val state = _state.asStateFlow()

    /**
     * Загружает данные заказа по ID.
     *
     * @param orderId ID заказа для загрузки
     */
    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.getOrder(orderId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            order = result.data,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Resource.Loading<*> -> {}
            }
        }
    }

    /**
     * Обновляет статус заказа.
     *
     * После успешного обновления перезагружает данные заказа.
     *
     * @param orderId ID заказа для обновления
     * @param newStatus Новый статус заказа ("Pending", "Processing", "Completed", "Cancelled")
     */
    fun updateOrderStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val orderUpdateDTO = OrderUpdateDTO(status = newStatus)
            when (val result = orderRepository.updateOrderStatus(orderId, orderUpdateDTO)) {
                is Resource.Success -> {
                    loadOrder(orderId)
                    _state.update { it.copy(statusUpdated = true) }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Resource.Loading<*> -> {}
            }
        }
    }

    /**
     * Удаляет заказ из системы.
     *
     * @param orderId ID заказа для удаления
     */
    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.deleteOrder(orderId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            orderDeleted = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Resource.Loading<*> -> {}
            }
        }
    }
}