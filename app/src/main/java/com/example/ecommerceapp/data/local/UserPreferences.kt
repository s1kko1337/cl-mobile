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

    val authToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    val userId: Flow<Int?> = dataStore.data.map { it[USER_ID_KEY] }
    val userRole: Flow<String?> = dataStore.data.map { it[ROLE_KEY] }
    val username: Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }
    val email: Flow<String?> = dataStore.data.map { it[EMAIL_KEY] }

    suspend fun saveAuthData(token: String, userId: Int, role: String, username: String, email: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[ROLE_KEY] = role
            prefs[USERNAME_KEY] = username
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearAuthData() {
        dataStore.edit { it.clear() }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[TOKEN_KEY] != null }

    // Методы для работы с адресами доставки
    val deliveryAddresses: Flow<List<DeliveryAddress>> = dataStore.data.map { prefs ->
        val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: return@map emptyList()
        try {
            json.decodeFromString<List<DeliveryAddress>>(addressesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveDeliveryAddresses(addresses: List<DeliveryAddress>) {
        dataStore.edit { prefs ->
            prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
        }
    }

    suspend fun addDeliveryAddress(address: DeliveryAddress) {
        val currentAddresses = deliveryAddresses.map { it }.toString()
        dataStore.edit { prefs ->
            val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: "[]"
            val addresses = try {
                json.decodeFromString<MutableList<DeliveryAddress>>(addressesJson)
            } catch (e: Exception) {
                mutableListOf()
            }

            // Если новый адрес по умолчанию, сбрасываем флаг у остальных
            if (address.isDefault) {
                addresses.replaceAll { it.copy(isDefault = false) }
            }

            addresses.add(address)
            prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
        }
    }

    suspend fun updateDeliveryAddress(index: Int, address: DeliveryAddress) {
        dataStore.edit { prefs ->
            val addressesJson = prefs[DELIVERY_ADDRESSES_KEY] ?: "[]"
            val addresses = try {
                json.decodeFromString<MutableList<DeliveryAddress>>(addressesJson)
            } catch (e: Exception) {
                mutableListOf()
            }

            if (index in addresses.indices) {
                // Если новый адрес по умолчанию, сбрасываем флаг у остальных
                if (address.isDefault) {
                    addresses.replaceAll { it.copy(isDefault = false) }
                }
                addresses[index] = address
                prefs[DELIVERY_ADDRESSES_KEY] = json.encodeToString(addresses)
            }
        }
    }

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