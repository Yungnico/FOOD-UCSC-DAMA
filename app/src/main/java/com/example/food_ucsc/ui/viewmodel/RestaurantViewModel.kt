package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.Menu
import com.example.food_ucsc.ui.models.Restaurant
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
            
            try {
                // Fetch from repository
                val restaurant = foodRepository.getRestaurantById(restaurantId)
                val menus = foodRepository.getMenusByRestaurant(restaurantId)

                _uiState.update { 
                    it.copy(
                        restaurant = restaurant,
                        menus = menus,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                // Fallback to mock data if API fails or for testing
                loadMockData(restaurantId)
            }
        }
    }

    private fun loadMockData(restaurantId: Int) {
        val restaurant = Restaurant(
            id = restaurantId,
            nombre = "Casino Central (Mock)",
            descripcion = "Almuerzos y colaciones para la comunidad universitaria.",
            horario = "08:00 - 18:00",
            contacto = "+56 41 273 5000",
            latitude = -36.801,
            longitude = -73.013,
            tiempo_espera_estimado = "15-20 min",
            rating = 4.5,
            icon = Icons.Default.Restaurant,
            bannerColor = 0xFF6750A4
        )

        val products = listOf(
            FoodItem(
                id = 1,
                nombre = "Almuerzo Ejecutivo",
                descripcion = "Entrada, fondo y postre",
                precio_base = 3500.0,
                categoria_basica = "Almuerzo",
                stock = 50,
                icon = Icons.Default.Restaurant
            )
        )

        val menus = listOf(
            Menu(
                id = 1,
                local_id = restaurantId,
                fecha = "2023-10-27",
                titulo = "Menú del Día",
                promociones = "10% de descuento con credencial",
                productos = products
            )
        )

        _uiState.update { 
            it.copy(
                restaurant = restaurant,
                menus = menus,
                isLoading = false
            )
        }
    }
}
