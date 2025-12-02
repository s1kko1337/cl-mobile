package com.example.ecommerceapp.ui.customer.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.local.UserPreferences
import com.example.ecommerceapp.data.model.CartItem
import com.example.ecommerceapp.data.model.DeliveryAddress
import com.example.ecommerceapp.data.model.OrderCreateDTO
import com.example.ecommerceapp.data.model.OrderItemCreateDTO
import com.example.ecommerceapp.data.repository.CartRepository
import com.example.ecommerceapp.data.repository.OrderRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана оформления заказа.
 *
 * @property items Список товаров в корзине для оформления
 * @property total Общая стоимость заказа
 * @property savedAddresses Список сохранённых адресов доставки пользователя
 * @property isProcessing Индикатор обработки заказа
 * @property orderCompleted Флаг успешного оформления заказа
 * @property error Сообщение об ошибке (null если ошибок нет)
 */
data class CheckoutState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val savedAddresses: List<DeliveryAddress> = emptyList(),
    val isProcessing: Boolean = false,
    val orderCompleted: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel для экрана оформления заказа.
 *
 * Управляет процессом оформления заказа: отображает товары из корзины,
 * рассчитывает общую стоимость, управляет адресами доставки
 * и отправляет заказ на сервер.
 *
 * @property cartRepository Репозиторий для работы с корзиной
 * @property orderRepository Репозиторий для создания заказов
 * @property userPreferences Менеджер пользовательских настроек для работы с адресами
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())

    /**
     * Реактивный поток состояния экрана оформления заказа.
     */
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _state.update {
                    it.copy(
                        items = items,
                        total = items.sumOf { item -> item.price * item.quantity }
                    )
                }
            }
        }

        viewModelScope.launch {
            userPreferences.deliveryAddresses.collect { addresses ->
                _state.update { it.copy(savedAddresses = addresses) }
            }
        }
    }

    /**
     * Оформляет заказ с указанными данными.
     *
     * Преобразует товары из корзины в элементы заказа,
     * отправляет заказ на сервер и очищает корзину после успешного создания.
     * Поддерживает методы оплаты "card" (карта) и "cash" (наличные).
     *
     * @param name Имя получателя заказа
     * @param address Адрес доставки
     * @param phone Контактный телефон
     * @param paymentMethod Способ оплаты ("card" или "cash")
     */
    fun placeOrder(
        name: String,
        address: String,
        phone: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, error = null) }

            val method = when (paymentMethod) {
                "card" -> "Card"
                "cash" -> "Cash"
                else -> "Card"
            }

            val orderItems = _state.value.items.map { item ->
                OrderItemCreateDTO(
                    productId = item.productId,
                    quantity = item.quantity
                )
            }

            val orderCreateDTO = OrderCreateDTO(
                customerName = name,
                customerPhone = phone,
                deliveryAddress = address,
                paymentMethod = method,
                orderItems = orderItems
            )

            when (val result = orderRepository.createOrder(orderCreateDTO)) {
                is Resource.Success -> {
                    cartRepository.clearCart()
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            orderCompleted = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            error = result.message
                        )
                    }
                }

                is Resource.Loading<*> -> TODO()
            }
        }
    }

    /**
     * Очищает сообщение об ошибке в состоянии.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * Сохраняет новый адрес доставки в настройках пользователя.
     *
     * Создаёт объект адреса доставки с указанными данными и координатами,
     * и добавляет его в список сохранённых адресов.
     *
     * @param name Имя получателя
     * @param phone Контактный телефон
     * @param address Текстовое описание адреса
     * @param latitude Географическая широта (необязательно)
     * @param longitude Географическая долгота (необязательно)
     * @param setAsDefault Установить ли данный адрес как адрес по умолчанию
     */
    fun saveDeliveryAddress(
        name: String,
        phone: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
        setAsDefault: Boolean
    ) {
        viewModelScope.launch {
            val deliveryAddress = DeliveryAddress(
                name = name,
                phone = phone,
                address = address,
                latitude = latitude,
                longitude = longitude,
                isDefault = setAsDefault
            )
            userPreferences.addDeliveryAddress(deliveryAddress)
        }
    }
}