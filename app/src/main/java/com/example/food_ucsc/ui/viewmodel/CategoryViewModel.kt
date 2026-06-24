package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.data.remote.toFoodItem
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
            
            runCatching {
                val restaurants = foodRepository.getRestaurants()
                val categories = foodRepository.getCategories()
                val products = foodRepository.getProductDetails()

                if (categoryName == "Otros") {
                    _uiState.update {
                        it.copy(
                            categories = categories.ifEmpty {
                                listOf(
                                    Category("Comida Rápida", Icons.Default.Fastfood),
                                    Category("Saludable", Icons.Default.Restaurant),
                                    Category("Vegetariana", Icons.Default.Grass),
                                    Category("Vegana", Icons.Default.Eco),
                                    Category("Postres", Icons.Default.Cake),
                                    Category("Otros", Icons.Default.MoreHoriz)
                                )
                            },
                            isLoading = false
                        )
                    }
                    return@runCatching
                }

                val matchingProducts = products.filter {
                    it.categoriaBasica.equals(categoryName, ignoreCase = true) ||
                        it.categoriaBasica.contains(categoryName, ignoreCase = true) ||
                        it.categorias.any { categoria -> categoria.nombre.equals(categoryName, ignoreCase = true) }
                }

                val restaurantIds = matchingProducts
                    .flatMap { product -> product.menus.map { it.localId } }
                    .distinct()

                val matchedRestaurants = restaurants.filter { it.id in restaurantIds }

                _uiState.update {
                    it.copy(
                        restaurants = matchedRestaurants,
                        items = matchingProducts.map { it.toFoodItem() },
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        error = it.error ?: "No se pudieron cargar los datos de la categoría",
                        isLoading = false
                    )
                }
            }
        }
    }
}
