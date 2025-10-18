package com.example.ecommerceapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val ROLE_KEY = stringPreferencesKey("role")
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    val authToken: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    val userId: Flow<Int?> = dataStore.data.map { it[USER_ID_KEY] }
    val userRole: Flow<String?> = dataStore.data.map { it[ROLE_KEY] }
    val username: Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveAuthData(token: String, userId: Int, role: String, username: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[ROLE_KEY] = role
            prefs[USERNAME_KEY] = username
        }
    }

    suspend fun clearAuthData() {
        dataStore.edit { it.clear() }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[TOKEN_KEY] != null }
}