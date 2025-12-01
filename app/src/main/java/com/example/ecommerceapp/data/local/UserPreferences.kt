package com.example.ecommerceapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.ecommerceapp.data.model.DeliveryAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Менеджер пользовательских настроек на основе DataStore.
 *
 * Управляет хранением данных аутентификации пользователя и адресов доставки.
 * Использует DataStore Preferences API для асинхронного реактивного доступа к данным.
 * Адреса доставки сериализуются в JSON для хранения.
 *
 * @property dataStore Экземпляр DataStore для доступа к хранилищу
 * @property json Экземпляр Json для сериализации/десериализации адресов
 */
class UserPreferences(context: Context) {
    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val ROLE_KEY = stringPreferencesKey("role")
        val USERNAME_KEY = stringPreferencesKey("username")
        val EMAIL_KEY = stringPreferencesKey("email")
        val DELIVERY_ADDRESSES_KEY = stringPreferencesKey("delivery_addresses")
    }

    /**
     * Реактивный поток с токеном аутентификации пользователя.
     */
    val authToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }

    /**
     * Реактивный поток с ID пользователя.
     */
    val userId: Flow<Int?> = dataStore.data.map { it[USER_ID_KEY] }

    /**
     * Реактивный поток с ролью пользователя (admin/customer).
     */
    val userRole: Flow<String?> = dataStore.data.map { it[ROLE_KEY] }

    /**
     * Реактивный поток с именем пользователя.
     */
    val username: Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }

    /**
     * Реактивный поток с email пользователя.
     */
    val email: Flow<String?> = dataStore.data.map { it[EMAIL_KEY] }

    /**
     * Сохраняет данные аутентификации пользователя.
     *
     * @param token Токен аутентификации
     * @param userId ID пользователя
     * @param role Роль пользователя (admin/customer)
     * @param username Имя пользователя
     * @param email Email пользователя
     */
    suspend fun saveAuthData(token: String, userId: Int, role: String, username: String, email: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[ROLE_KEY] = role
            prefs[USERNAME_KEY] = username
            prefs[EMAIL_KEY] = email
        }
    }

    /**
     * Очищает все данные аутентификации (выход из системы).
     */
    suspend fun clearAuthData() {
        dataStore.edit { it.clear() }
    }

    /**
     * Реактивный поток, указывающий, авторизован ли пользователь.
     */
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[TOKEN_KEY] != null }

    /**
     * Реактивный поток со списком всех адресов доставки пользователя.
     *
     * Адреса десериализуются из JSON. В случае ошибки возвращается пустой список.
     */
    val deliveryAddresses: Flow<List<DeliveryAddress>> = dataStore.data.map { prefs ->
        val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: return@map emptyList()
        try {
            json.decodeFromString<List<DeliveryAddress>>(addressesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Сохраняет список адресов доставки, заменяя существующий.
     *
     * @param addresses Список адресов доставки для сохранения
     */
    suspend fun saveDeliveryAddresses(addresses: List<DeliveryAddress>) {
        dataStore.edit { prefs ->
            prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
        }
    }

    /**
     * Добавляет новый адрес доставки к существующим.
     *
     * Если добавляемый адрес помечен как "по умолчанию", снимает этот флаг со всех остальных адресов.
     *
     * @param address Новый адрес доставки для добавления
     */
    suspend fun addDeliveryAddress(address: DeliveryAddress) {
        val currentAddresses = deliveryAddresses.map { it }.toString()
        dataStore.edit { prefs ->
            val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: "[]"
            val addresses = try {
                json.decodeFromString<MutableList<DeliveryAddress>>(addressesJson)
            } catch (e: Exception) {
                mutableListOf()
            }

            if (address.isDefault) {
                addresses.replaceAll { it.copy(isDefault = false) }
            }

            addresses.add(address)
            prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
        }
    }

    /**
     * Обновляет существующий адрес доставки по индексу.
     *
     * Если обновляемый адрес помечен как "по умолчанию", снимает этот флаг со всех остальных адресов.
     *
     * @param index Индекс адреса в списке для обновления
     * @param address Новые данные адреса
     */
    suspend fun updateDeliveryAddress(index: Int, address: DeliveryAddress) {
        dataStore.edit { prefs ->
            val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: "[]"
            val addresses = try {
                json.decodeFromString<MutableList<DeliveryAddress>>(addressesJson)
            } catch (e: Exception) {
                mutableListOf()
            }

            if (index in addresses.indices) {
                if (address.isDefault) {
                    addresses.replaceAll { it.copy(isDefault = false) }
                }
                addresses[index] = address
                prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
            }
        }
    }

    /**
     * Удаляет адрес доставки по индексу.
     *
     * @param index Индекс адреса в списке для удаления
     */
    suspend fun deleteDeliveryAddress(index: Int) {
        dataStore.edit { prefs ->
            val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: "[]"
            val addresses = try {
                json.decodeFromString<MutableList<DeliveryAddress>>(addressesJson)
            } catch (e: Exception) {
                mutableListOf()
            }

            if (index in addresses.indices) {
                addresses.removeAt(index)
                prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
            }
        }
    }
}