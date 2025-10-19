package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.UserPreferences
import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val userPrefs: UserPreferences
) {
    suspend fun register(username: String, email: String, password: String, role: String): Resource<Unit> {
        return try {
            val response = api.register(RegisterRequest(username, email, password, role))
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun login(username: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                userPrefs.saveAuthData(
                    authResponse.token,
                    authResponse.user.id,
                    authResponse.user.role,
                    authResponse.user.login
                )
                Resource.Success(authResponse)
            } else {
                Resource.Error(response.message() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun logout() {
        userPrefs.clearAuthData()
    }

    val isLoggedIn: Flow<Boolean> = userPrefs.isLoggedIn
    val userRole: Flow<String?> = userPrefs.userRole
    val username: Flow<String?> = userPrefs.username
}
