package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.state.RestaurantDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RestaurantDetailUiState())
    val uiState: StateFlow<RestaurantDetailUiState> = _uiState.asStateFlow()

    fun loadRestaurantDetails(restaurantId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            runCatching {
                val restaurant = foodRepository.getRestaurantById(restaurantId)
                val menus = foodRepository.getMenusByRestaurant(restaurantId)

                _uiState.update {
                    it.copy(
                        restaurant = restaurant,
                        menus = menus,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = ex.message ?: "No se pudo cargar el detalle del local"
                    )
                }
            }
        }
    }
}
