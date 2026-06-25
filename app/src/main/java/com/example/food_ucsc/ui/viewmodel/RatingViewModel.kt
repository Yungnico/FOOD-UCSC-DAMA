package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RatingUiState(
    val orderRating: Int = 0,
    val restaurantRating: Int = 0,
    val comment: String = "",
    val isSubmitted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val purchaseId: Int? = null
)

class RatingViewModel(
    private val repository: FoodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

    fun setPurchaseId(purchaseId: Int) {
        _uiState.update { it.copy(purchaseId = purchaseId) }
    }

    fun updateOrderRating(rating: Int) {
        _uiState.update { it.copy(orderRating = rating) }
    }

    fun updateRestaurantRating(rating: Int) {
        _uiState.update { it.copy(restaurantRating = rating) }
    }

    fun updateComment(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun submitRating() {
        val purchaseId = _uiState.value.purchaseId

        if (purchaseId == null) {
            _uiState.update {
                it.copy(errorMessage = "No se puede enviar la calificación sin una compra activa")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.ratePurchase(purchaseId, _uiState.value.orderRating)
            }.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, isSubmitted = true, errorMessage = null)
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "No se pudo enviar la calificación"
                    )
                }
            }
        }
    }
}
