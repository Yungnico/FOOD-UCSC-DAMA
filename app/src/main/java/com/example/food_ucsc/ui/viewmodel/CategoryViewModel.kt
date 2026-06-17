package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.state.CategoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun loadCategoryItems(categoryName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(categoryName = categoryName, isLoading = true, categories = emptyList()) }
            
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
                // Simulación de filtrado de platos por categoría
                val items = listOf(
                    FoodItem(1, "Plato de $categoryName 1", "Descripción del plato", 5000.0, categoryName, Icons.Default.Restaurant),
                    FoodItem(2, "Plato de $categoryName 2", "Descripción del plato", 4500.0, categoryName, Icons.Default.Restaurant)
                )

                _uiState.update { 
                    it.copy(
                        items = items,
                        isLoading = false
                    ) 
                }
            }
        }
    }
}
