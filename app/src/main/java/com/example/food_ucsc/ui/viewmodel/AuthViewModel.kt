package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: FoodRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email y contraseña son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.login(email.trim(), password)
            }.onSuccess { (token, user) ->
                sessionManager.saveSession(token, user.id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        errorMessage = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = ex.message ?: "No se pudo iniciar sesión"
                    )
                }
            }
        }
    }

    fun register(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        email: String,
        password: String
    ) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre, email y contraseña son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.register(nombre, apellidoPaterno, apellidoMaterno, email.trim(), password)
            }.onSuccess { (token, user) ->
                sessionManager.saveSession(token, user.id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        errorMessage = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = ex.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun checkSession() {
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isAuthenticated = false, user = null) }
            return
        }

        viewModelScope.launch {
            runCatching { repository.me(token) }
                .onSuccess { user ->
                    sessionManager.saveSession(token, user.id)
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            user = user,
                            errorMessage = null
                        )
                    }
                }
                .onFailure {
                    sessionManager.clearSession()
                    _uiState.update {
                        it.copy(
                            isAuthenticated = false,
                            user = null,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun logout() {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            if (!token.isNullOrBlank()) {
                runCatching { repository.logout(token) }
            }
            sessionManager.clearSession()
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    user = null,
                    errorMessage = null,
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
