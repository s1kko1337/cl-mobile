package com.example.ecommerceapp.data.model

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

data class OrderItemDTO(
    val id: Int,
    val productId: Int,
    val productName: String,
    val priceAtPurchase: Double,
    val quantity: Int,
    val subtotal: Double
)

data class OrderCreateDTO(
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val orderItems: List<OrderItemCreateDTO>
)

data class OrderItemCreateDTO(
    val productId: Int,
    val quantity: Int
)

data class OrderUpdateDTO(
    val status: String
)