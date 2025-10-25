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

enum class OrderSortType {
    DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
}

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminOrdersState())
    val state = _state.asStateFlow()

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

                is Resource.Loading<*> -> TODO()
            }
        }
    }

    fun filterByStatus(status: String?) {
        _state.update {
            it.copy(
                selectedFilter = status,
                filteredOrders = applyFiltersAndSort(it.orders, status, it.currentSort, it.searchQuery)
            )
        }
    }

    fun sortOrders(sortType: OrderSortType) {
        _state.update {
            it.copy(
                currentSort = sortType,
                filteredOrders = applyFiltersAndSort(it.orders, it.selectedFilter, sortType, it.searchQuery)
            )
        }
    }

    fun searchOrders(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredOrders = applyFiltersAndSort(it.orders, it.selectedFilter, it.currentSort, query)
            )
        }
    }

    private fun applyFiltersAndSort(
        orders: List<OrderDTO>,
        statusFilter: String?,
        sortType: OrderSortType,
        searchQuery: String
    ): List<OrderDTO> {
        var result = orders

        // Применяем фильтр по статусу
        if (statusFilter != null) {
            result = result.filter { it.status == statusFilter }
        }

        // Применяем поиск
        if (searchQuery.isNotBlank()) {
            result = result.filter { order ->
                order.id.toString().contains(searchQuery) ||
                        order.customerName.contains(searchQuery, ignoreCase = true) ||
                        order.customerPhone.contains(searchQuery)
            }
        }

        // Применяем сортировку
        result = when (sortType) {
            OrderSortType.DATE_DESC -> result.sortedByDescending { it.createdAt }
            OrderSortType.DATE_ASC -> result.sortedBy { it.createdAt }
            OrderSortType.AMOUNT_DESC -> result.sortedByDescending { it.totalAmount }
            OrderSortType.AMOUNT_ASC -> result.sortedBy { it.totalAmount }
        }

        return result
    }
}