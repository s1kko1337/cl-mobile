package com.example.ecommerceapp.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Int,
    val role: String,
    val username: String
)
