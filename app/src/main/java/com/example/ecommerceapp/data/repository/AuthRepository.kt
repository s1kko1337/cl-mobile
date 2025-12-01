package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.UserPreferences
import com.example.ecommerceapp.data.model.*
import com.example.ecommerceapp.data.remote.ApiService
import com.example.ecommerceapp.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Репозиторий для работы с аутентификацией и авторизацией пользователей.
 *
 * Предоставляет методы для регистрации, входа, выхода и изменения пароля,
 * а также управляет данными пользователя в локальном хранилище.
 *
 * @property api Сервис API для выполнения сетевых запросов
 * @property userPrefs Локальное хранилище данных пользователя
 */
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val userPrefs: UserPreferences
) {
    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param username Имя пользователя (логин)
     * @param email Email адрес
     * @param password Пароль
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun register(username: String, email: String, password: String): Resource<Unit> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * При успешном входе сохраняет токен и данные пользователя в локальное хранилище.
     *
     * @param email Email или username пользователя
     * @param password Пароль
     * @return Resource с AuthResponse или сообщением об ошибке
     */
    suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                userPrefs.saveAuthData(
                    authResponse.token,
                    authResponse.user.id,
                    authResponse.user.role,
                    authResponse.user.username,
                    email
                )
                Resource.Success(authResponse)
            } else {
                Resource.Error(response.message() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /**
     * Выполняет выход пользователя из системы.
     *
     * Очищает все сохраненные данные аутентификации из локального хранилища.
     */
    suspend fun logout() {
        userPrefs.clearAuthData()
    }

    /**
     * Изменяет пароль текущего пользователя.
     *
     * @param currentPassword Текущий пароль пользователя
     * @param newPassword Новый пароль
     * @param confirmPassword Подтверждение нового пароля
     * @return Resource с Unit или сообщением об ошибке
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Resource<Unit> {
        return try {
            val response = api.changePassword(
                ChangePasswordRequest(currentPassword, newPassword, confirmPassword)
            )
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Ошибка смены пароля")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка сети")
        }
    }

    /**
     * Flow статуса входа пользователя.
     */
    val isLoggedIn: Flow<Boolean> = userPrefs.isLoggedIn

    /**
     * Flow роли пользователя ("user" или "admin").
     */
    val userRole: Flow<String?> = userPrefs.userRole

    /**
     * Flow имени пользователя.
     */
    val username: Flow<String?> = userPrefs.username

    /**
     * Flow email пользователя.
     */
    val email: Flow<String?> = userPrefs.email
}
