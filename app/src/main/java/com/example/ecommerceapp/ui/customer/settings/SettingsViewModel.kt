package com.example.ecommerceapp.ui.customer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.local.UserPreferences
import com.example.ecommerceapp.data.model.DeliveryAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val addresses: List<DeliveryAddress> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            userPreferences.deliveryAddresses.collect { addresses ->
                _state.update { it.copy(addresses = addresses) }
            }
        }
    }

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

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
