package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.state.HomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val foodRepository: FoodRepository) : ViewModel() {
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
                FoodItem(
                    id = 1,
                    nombre = "T Shirts",
                    descripcion = "Description",
                    precio_base = 10.0,
                    categoria_basica = "Otros",
                    stock = 10,
                    icon = Icons.Default.Checkroom
                ),
                FoodItem(
                    id = 2,
                    nombre = "Trousers",
                    descripcion = "Description",
                    precio_base = 15.0,
                    categoria_basica = "Otros",
                    stock = 10,
                    icon = Icons.Default.Checkroom
                ),
                FoodItem(
                    id = 3,
                    nombre = "Bag",
                    descripcion = "Description",
                    precio_base = 20.0,
                    categoria_basica = "Otros",
                    stock = 10,
                    icon = Icons.Default.ShoppingBag
                )
            )

            val favorites = listOf(
                FoodItem(
                    id = 4,
                    nombre = "Laptop",
                    descripcion = "Description",
                    precio_base = 1000.0,
                    categoria_basica = "Otros",
                    stock = 10,
                    icon = Icons.Default.Laptop
                ),
                FoodItem(
                    id = 5,
                    nombre = "Weekend",
                    descripcion = "Description",
                    precio_base = 50.0,
                    categoria_basica = "Otros",
                    stock = 10,
                    icon = Icons.Default.Weekend
                ),
                FoodItem(
                    id = 6,
                    nombre = "Restaurant",
                    descripcion = "Description",
                    precio_base = 30.0,
                    categoria_basica = "Saludable",
                    stock = 10,
                    icon = Icons.Default.Restaurant
                )
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
