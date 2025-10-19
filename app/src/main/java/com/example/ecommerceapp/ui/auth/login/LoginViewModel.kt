package com.example.ecommerceapp.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val role: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState(isLoading = true)
            when (val result = authRepository.login(username, password)) {
                is Resource.Success -> {
                    _state.value = LoginState(role = result.data?.user?.role)
                }
                is Resource.Error -> {
                    _state.value = LoginState(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}