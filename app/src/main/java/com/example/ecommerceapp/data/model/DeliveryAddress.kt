package com.example.ecommerceapp.data.model

import kotlinx.serialization.Serializable

/**
 * Модель адреса доставки.
 *
 * Используется для хранения и передачи информации об адресе доставки заказа.
 * Поддерживает сериализацию для сохранения в SharedPreferences.
 *
 * @property name Имя получателя
 * @property phone Телефон получателя
 * @property address Текстовый адрес доставки
 * @property latitude Широта координат адреса (опционально)
 * @property longitude Долгота координат адреса (опционально)
 * @property isDefault Флаг, является ли этот адрес адресом по умолчанию
 */
@Serializable
data class DeliveryAddress(
    val name: String,
    val phone: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)
