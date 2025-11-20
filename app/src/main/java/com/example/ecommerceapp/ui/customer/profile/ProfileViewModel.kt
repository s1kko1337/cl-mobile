package com.example.ecommerceapp.ui.customer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val isChangingPassword: Boolean = false,
    val changePasswordSuccess: Boolean = false,
    val changePasswordError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                authRepository.username,
                authRepository.email,
                authRepository.userRole
            ) { username, email, role ->
                _state.update { it.copy(
                    username = username ?: "",
                    email = email ?: "",
                    role = role ?: ""
                )}
            }.collect()
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(
                isChangingPassword = true,
                changePasswordError = null,
                changePasswordSuccess = false
            )}

            when (val result = authRepository.changePassword(currentPassword, newPassword, confirmPassword)) {
                is Resource.Success -> {
                    _state.update { it.copy(
                        isChangingPassword = false,
                        changePasswordSuccess = true
                    )}
                }
                is Resource.Error -> {
                    _state.update { it.copy(
                        isChangingPassword = false,
                        changePasswordError = result.message
                    )}
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearChangePasswordState() {
        _state.update { it.copy(
            changePasswordSuccess = false,
            changePasswordError = null
        )}
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}