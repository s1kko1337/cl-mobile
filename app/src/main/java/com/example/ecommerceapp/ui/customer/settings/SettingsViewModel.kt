package com.example.ecommerceapp.ui.customer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.local.UserPreferences
import com.example.ecommerceapp.data.model.DeliveryAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояние экрана настроек пользователя.
 *
 * @property addresses Список сохранённых адресов доставки
 * @property isLoading Индикатор выполнения операции с адресами
 * @property error Сообщение об ошибке (null если ошибок нет)
 */
data class SettingsState(
    val addresses: List<DeliveryAddress> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel для экрана настроек пользователя.
 *
 * Управляет списком сохранённых адресов доставки пользователя:
 * загрузка, добавление, обновление и удаление адресов.
 *
 * @property userPreferences Менеджер пользовательских настроек
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())

    /**
     * Реактивный поток состояния экрана настроек.
     */
    val state = _state.asStateFlow()

    init {
        loadAddresses()
    }

    /**
     * Загружает список сохранённых адресов доставки из настроек пользователя.
     */
    private fun loadAddresses() {
        viewModelScope.launch {
            userPreferences.deliveryAddresses.collect { addresses ->
                _state.update { it.copy(addresses = addresses) }
            }
        }
    }

    /**
     * Добавляет новый адрес доставки в список сохранённых.
     *
     * @param address Новый адрес для сохранения
     */
    fun addAddress(address: DeliveryAddress) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                userPreferences.addDeliveryAddress(address)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка сохранения адреса"
                    )
                }
            }
        }
    }

    /**
     * Обновляет существующий адрес доставки по индексу.
     *
     * @param index Индекс адреса в списке
     * @param address Новые данные адреса
     */
    fun updateAddress(index: Int, address: DeliveryAddress) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                userPreferences.updateDeliveryAddress(index, address)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка обновления адреса"
                    )
                }
            }
        }
    }

    /**
     * Удаляет адрес доставки из списка по индексу.
     *
     * @param index Индекс адреса для удаления
     */
    fun deleteAddress(index: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                userPreferences.deleteDeliveryAddress(index)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка удаления адреса"
                    )
                }
            }
        }
    }

    /**
     * Очищает сообщение об ошибке в состоянии.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
