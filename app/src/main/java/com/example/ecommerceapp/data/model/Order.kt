package com.example.ecommerceapp.data.model

/**
 * DTO для представления заказа из API.
 *
 * @property id Уникальный идентификатор заказа
 * @property userId ID пользователя, оформившего заказ
 * @property customerName Имя покупателя
 * @property customerPhone Телефон покупателя
 * @property deliveryAddress Адрес доставки
 * @property paymentMethod Способ оплаты ("Card" или "Cash")
 * @property totalAmount Общая сумма заказа
 * @property status Статус заказа (pending, processing, completed, cancelled)
 * @property createdAt Дата и время создания заказа
 * @property updatedAt Дата и время последнего обновления заказа (опционально)
 * @property orderItems Список позиций в заказе
 */
data class OrderDTO(
    val id: Int,
    val userId: Int,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String?,
    val orderItems: List<OrderItemDTO>
)

/**
 * DTO для представления позиции заказа.
 *
 * @property id Уникальный идентификатор позиции заказа
 * @property productId ID продукта
 * @property productName Название продукта
 * @property priceAtPurchase Цена продукта на момент покупки
 * @property quantity Количество единиц продукта
 * @property subtotal Итоговая стоимость позиции (priceAtPurchase * quantity)
 */
data class OrderItemDTO(
    val id: Int,
    val productId: Int,
    val productName: String,
    val priceAtPurchase: Double,
    val quantity: Int,
    val subtotal: Double
)

/**
 * DTO для создания нового заказа.
 *
 * @property customerName Имя покупателя
 * @property customerPhone Телефон покупателя
 * @property deliveryAddress Адрес доставки
 * @property paymentMethod Способ оплаты ("Card" или "Cash")
 * @property orderItems Список позиций для заказа
 */
data class OrderCreateDTO(
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val orderItems: List<OrderItemCreateDTO>
)

/**
 * DTO для создания позиции заказа.
 *
 * @property productId ID продукта
 * @property quantity Количество единиц продукта
 */
data class OrderItemCreateDTO(
    val productId: Int,
    val quantity: Int
)

/**
 * DTO для обновления статуса заказа.
 *
 * @property status Новый статус заказа (pending, processing, completed, cancelled)
 */
data class OrderUpdateDTO(
    val status: String
)