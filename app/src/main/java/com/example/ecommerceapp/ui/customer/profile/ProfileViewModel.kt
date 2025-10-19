package com.example.ecommerceapp.ui.customer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val username: String = "",
    val role: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val state: StateFlow<ProfileState> = combine(
        authRepository.username,
        authRepository.userRole
    ) { username, role ->
        ProfileState(
            username = username ?: "",
            role = role ?: ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileState()
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}