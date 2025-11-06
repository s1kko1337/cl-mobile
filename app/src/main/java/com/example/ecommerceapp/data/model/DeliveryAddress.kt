package com.example.ecommerceapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryAddress(
    val name: String,
    val phone: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)
