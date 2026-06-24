package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderHistoryUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrderHistoryViewModel(
    private val repository: FoodRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    orders = emptyList(),
                    isLoading = false,
                    error = "No hay una sesión activa"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.getMyPurchases(token)
            }.onSuccess { orders ->
                _uiState.update {
                    it.copy(orders = orders, isLoading = false, error = null)
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        orders = emptyList(),
                        isLoading = false,
                        error = ex.message ?: "No se pudo cargar el historial"
                    )
                }
            }
        }
    }
}
