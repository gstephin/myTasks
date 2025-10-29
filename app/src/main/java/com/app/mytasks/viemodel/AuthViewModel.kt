package com.app.mytasks.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mytasks.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel
 *
 * @author stephingeorge
 * @date 29/10/2025
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
     val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthState.Loading
        repository.login(email, password)
            .onSuccess { user ->
                _uiState.value = AuthState.Success(user)
            }
            .onFailure { e ->
                _uiState.value = AuthState.Error(e.message ?: "Login failed")
            }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthState.Loading
        repository.register(email, password)
            .onSuccess { user ->
                _uiState.value = AuthState.Success(user)
            }
            .onFailure { e ->
                _uiState.value = AuthState.Error(e.message ?: "Registration failed")
            }
    }

    fun logout() = repository.logout()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}
