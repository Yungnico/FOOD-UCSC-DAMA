package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.state.HomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        checkPendingRatings()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val categories = listOf(
                Category("Comida Rápida", Icons.Default.Fastfood),
                Category("Saludable", Icons.Default.Restaurant),
                Category("Vegetariana", Icons.Default.Grass),
                Category("Vegana", Icons.Default.Eco),
                Category("Postres", Icons.Default.Cake),
                Category("Otros", Icons.Default.MoreHoriz)
            )

            val recommended = listOf(
                FoodItem(1, "T Shirts", "Description", 10.0, "Otros", Icons.Default.Checkroom),
                FoodItem(2, "Trousers", "Description", 15.0, "Otros", Icons.Default.Checkroom),
                FoodItem(3, "Bag", "Description", 20.0, "Otros", Icons.Default.ShoppingBag)
            )

            val favorites = listOf(
                FoodItem(4, "Laptop", "Description", 1000.0, "Otros", Icons.Default.Laptop),
                FoodItem(5, "Weekend", "Description", 50.0, "Otros", Icons.Default.Weekend),
                FoodItem(6, "Restaurant", "Description", 30.0, "Saludable", Icons.Default.Restaurant)
            )

            _uiState.update { 
                it.copy(
                    categories = categories,
                    recommendedItems = recommended,
                    favoriteItems = favorites,
                    isLoading = false
                ) 
            }
        }
    }

    private fun checkPendingRatings() {
        viewModelScope.launch {
            // Simula esperar "unos minutos" (usaremos 5 segundos para la demo)
            delay(5000)
            
            // Simula que existe un pedido pendiente de calificar
            _uiState.update { 
                it.copy(
                    showRatingDialog = true,
                    pendingOrderId = "101"
                ) 
            }
        }
    }

    fun dismissRatingDialog() {
        _uiState.update { it.copy(showRatingDialog = false) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
