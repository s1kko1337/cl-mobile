package com.example.ecommerceapp.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun register(username: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _state.value = RegisterState(isLoading = true)
            when (val result = authRepository.register(username, email, password, role)) {
                is Resource.Success -> {
                    _state.value = RegisterState(success = true)
                }
                is Resource.Error -> {
                    _state.value = RegisterState(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }
}