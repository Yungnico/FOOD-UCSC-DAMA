package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RatingUiState(
    val orderRating: Int = 0,
    val restaurantRating: Int = 0,
    val comment: String = "",
    val isSubmitted: Boolean = false
)

class RatingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

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
        // Here you would typically send the data to a repository/server
        _uiState.update { it.copy(isSubmitted = true) }
    }
}
