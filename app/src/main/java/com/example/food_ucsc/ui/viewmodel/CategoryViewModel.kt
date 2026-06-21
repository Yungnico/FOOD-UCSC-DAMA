package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.Restaurant
import com.example.food_ucsc.ui.state.CategoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun loadCategoryItems(categoryName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(categoryName = categoryName, isLoading = true, categories = emptyList()) }
            
            try {
                // In a real scenario, we might have an endpoint for categories
                // For now we use the repository to get restaurants and filter or show mock
                loadMockCategoryData(categoryName)
            } catch (e: Exception) {
                loadMockCategoryData(categoryName)
            }
        }
    }

    private fun loadMockCategoryData(categoryName: String) {
        if (categoryName == "Otros") {
            // Si la categoría es "Otros", cargamos todas las categorías
            val allCategories = listOf(
                Category("Comida Rápida", Icons.Default.Fastfood),
                Category("Saludable", Icons.Default.Restaurant),
                Category("Vegetariana", Icons.Default.Grass),
                Category("Vegana", Icons.Default.Eco),
                Category("Postres", Icons.Default.Cake),
                Category("Otros", Icons.Default.MoreHoriz)
            )
            _uiState.update { 
                it.copy(
                    categories = allCategories,
                    isLoading = false
                ) 
            }
        } else {
            // Simulación de locales que pertenecen a esta categoría
            val restaurants = listOf(
                Restaurant(
                    id = 1,
                    nombre = "Casino Central",
                    descripcion = "Especialistas en $categoryName",
                    horario = "08:00 - 18:00",
                    contacto = "+56 41 273 5000",
                    latitude = -36.801,
                    longitude = -73.013,
                    tiempo_espera_estatico = "15-20 min",
                    rating = 4.5,
                    icon = Icons.Default.Restaurant,
                    bannerColor = 0xFF6750A4
                )
            )

            // Simulación de platos destacados en esta categoría
            val items = listOf(
                FoodItem(1, "Especial de $categoryName", "Descripción del plato", 5000.0, categoryName, 10, Icons.Default.Restaurant),
                FoodItem(2, "Combo $categoryName", "Descripción del plato", 4500.0, categoryName, 15, Icons.Default.Restaurant)
            )

            _uiState.update { 
                it.copy(
                    restaurants = restaurants,
                    items = items,
                    isLoading = false
                )
            }
        }
    }
}
