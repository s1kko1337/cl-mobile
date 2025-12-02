package com.example.ecommerceapp.ui.admin.orders

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
 * Состояние экрана управления заказами администратором.
 *
 * @property orders Полный список заказов
 * @property filteredOrders Отфильтрованный и отсортированный список заказов
 * @property isLoading Индикатор загрузки списка заказов
 * @property error Сообщение об ошибке
 * @property selectedFilter Текущий фильтр по статусу заказа
 * @property currentSort Текущий тип сортировки
 * @property searchQuery Текущий поисковый запрос
 * @property totalOrders Общее количество заказов
 * @property pendingOrders Количество заказов со статусом "Pending"
 * @property processingOrders Количество заказов со статусом "Processing"
 * @property completedOrders Количество заказов со статусом "Completed"
 */
data class AdminOrdersState(
    val orders: List<OrderDTO> = emptyList(),
    val filteredOrders: List<OrderDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null,
    val currentSort: OrderSortType = OrderSortType.DATE_DESC,
    val searchQuery: String = "",
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val processingOrders: Int = 0,
    val completedOrders: Int = 0
)

/**
 * Типы сортировки заказов.
 *
 * @property DATE_DESC По дате создания (новые сначала)
 * @property DATE_ASC По дате создания (старые сначала)
 * @property AMOUNT_DESC По сумме (большие сначала)
 * @property AMOUNT_ASC По сумме (меньшие сначала)
 */
enum class OrderSortType {
    DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
}

/**
 * ViewModel для экрана управления заказами администратором.
 *
 * Управляет загрузкой, фильтрацией, сортировкой и поиском заказов.
 * Подсчитывает статистику по статусам заказов.
 *
 * @property orderRepository Репозиторий для работы с заказами
 */
@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminOrdersState())

    /** Реактивный поток состояния управления заказами */
    val state = _state.asStateFlow()

    /**
     * Загружает список всех заказов.
     *
     * Получает список заказов, применяет текущие фильтры и сортировку,
     * подсчитывает статистику по статусам.
     */
    fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.getOrders()) {
                is Resource.Success -> {
                    val orders = result.data ?: emptyList()
                    _state.update {
                        it.copy(
                            orders = orders,
                            filteredOrders = applyFiltersAndSort(orders, it.selectedFilter, it.currentSort, it.searchQuery),
                            isLoading = false,
                            totalOrders = orders.size,
                            pendingOrders = orders.count { order -> order.status == "Pending" },
                            processingOrders = orders.count { order -> order.status == "Processing" },
                            completedOrders = orders.count { order -> order.status == "Completed" }
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
     * Фильтрует заказы по статусу.
     *
     * @param status Статус для фильтрации ("Pending", "Processing", "Completed") или null для всех заказов
     */
    fun filterByStatus(status: String?) {
        _state.update {
            it.copy(
                selectedFilter = status,
                filteredOrders = applyFiltersAndSort(it.orders, status, it.currentSort, it.searchQuery)
            )
        }
    }

    /**
     * Изменяет тип сортировки заказов.
     *
     * @param sortType Новый тип сортировки
     */
    fun sortOrders(sortType: OrderSortType) {
        _state.update {
            it.copy(
                currentSort = sortType,
                filteredOrders = applyFiltersAndSort(it.orders, it.selectedFilter, sortType, it.searchQuery)
            )
        }
    }

    /**
     * Выполняет поиск заказов по запросу.
     *
     * Поиск осуществляется по ID заказа, имени и телефону клиента.
     *
     * @param query Поисковый запрос
     */
    fun searchOrders(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredOrders = applyFiltersAndSort(it.orders, it.selectedFilter, it.currentSort, query)
            )
        }
    }

    /**
     * Применяет фильтрацию, поиск и сортировку к списку заказов.
     *
     * Последовательно применяет фильтр по статусу, поисковый запрос
     * (по ID, имени клиента, телефону) и сортировку.
     *
     * @param orders Исходный список заказов
     * @param statusFilter Фильтр по статусу или null
     * @param sortType Тип сортировки
     * @param searchQuery Поисковый запрос
     * @return Отфильтрованный и отсортированный список заказов
     */
    private fun applyFiltersAndSort(
        orders: List<OrderDTO>,
        statusFilter: String?,
        sortType: OrderSortType,
        searchQuery: String
    ): List<OrderDTO> {
        var result = orders

        if (statusFilter != null) {
            result = result.filter { it.status == statusFilter }
        }

        if (searchQuery.isNotBlank()) {
            result = result.filter { order ->
                order.id.toString().contains(searchQuery) ||
                        order.customerName.contains(searchQuery, ignoreCase = true) ||
                        order.customerPhone.contains(searchQuery)
            }
        }

        result = when (sortType) {
            OrderSortType.DATE_DESC -> result.sortedByDescending { it.createdAt }
            OrderSortType.DATE_ASC -> result.sortedBy { it.createdAt }
            OrderSortType.AMOUNT_DESC -> result.sortedByDescending { it.totalAmount }
            OrderSortType.AMOUNT_ASC -> result.sortedBy { it.totalAmount }
        }

        return result
    }
}