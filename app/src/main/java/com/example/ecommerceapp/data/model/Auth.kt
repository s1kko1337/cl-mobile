package com.example.ecommerceapp.data.model

/**
 * Запрос на регистрацию нового пользователя.
 *
 * @property username Имя пользователя (логин)
 * @property email Email адрес пользователя
 * @property password Пароль пользователя
 * @property role Роль пользователя (по умолчанию "user", может быть "admin")
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user"
)

/**
 * Запрос на аутентификацию пользователя.
 *
 * @property username Имя пользователя (логин)
 * @property password Пароль пользователя
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Данные пользователя.
 *
 * @property id Уникальный идентификатор пользователя
 * @property username Имя пользователя (логин)
 * @property role Роль пользователя ("user" или "admin")
 */
data class UserData(
    val id: Int,
    val username: String,
    val role: String,
)

/**
 * Ответ сервера при успешной аутентификации.
 *
 * @property success Статус успешности операции
 * @property message Сообщение от сервера
 * @property user Данные пользователя
 * @property token JWT токен для авторизации последующих запросов
 */
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserData,
    val token: String
)

/**
 * Запрос на изменение пароля пользователя.
 *
 * @property currentPassword Текущий пароль пользователя
 * @property newPassword Новый пароль
 * @property confirmPassword Подтверждение нового пароля (должно совпадать с newPassword)
 */
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)
