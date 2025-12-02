package com.example.ecommerceapp.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.OrderDTO
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана списка заказов пользователя.
 *
 * @property orders Список заказов пользователя
 * @property isLoading Индикатор загрузки списка заказов
 * @property error Сообщение об ошибке (null если ошибок нет)
 */
data class OrdersState(
    val orders: List<OrderDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel для экрана списка заказов пользователя.
 *
 * Управляет загрузкой и отображением истории заказов текущего пользователя.
 *
 * @property orderRepository Репозиторий для работы с заказами
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersState())

    /**
     * Реактивный поток состояния списка заказов.
     */
    val state = _state.asStateFlow()

    /**
     * Загружает список заказов текущего пользователя с сервера.
     *
     * Обновляет состояние в зависимости от результата запроса.
     */
    fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.getOrders()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            orders = result.data ?: emptyList(),
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

                is Resource.Loading<*> -> TODO()
            }
        }
    }
}