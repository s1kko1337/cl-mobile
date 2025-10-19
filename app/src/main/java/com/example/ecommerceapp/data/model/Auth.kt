package com.example.ecommerceapp.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user"
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class UserData(
    val id: Int,
    val login: String,
    val role: String,
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserData,
    val token: String
)
