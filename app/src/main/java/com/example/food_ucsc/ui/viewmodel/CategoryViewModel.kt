package com.example.food_ucsc.ui.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.data.remote.toFoodItem
import com.example.food_ucsc.ui.state.CategoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val foodRepository: FoodRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun loadCategoryItems(categoryName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(categoryName = categoryName, isLoading = true, categories = emptyList()) }
            
            runCatching {
                val restaurants = foodRepository.getRestaurants()
                val categories = foodRepository.getCategories()
                val products = foodRepository.getProductDetails()
                
                // Cargar favoritos
                val favorites = try {
                    foodRepository.getMyFavoritesRaw()
                } catch (e: Exception) {
                    emptyList()
                }
                val favIds = favorites.map { it.productoId }.toSet()
                val favMap = favorites.associate { it.productoId to it.id }

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
                            favoriteProductIds = favIds,
                            favoriteMap = favMap,
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
                        favoriteProductIds = favIds,
                        favoriteMap = favMap,
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

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            val isFavorite = _uiState.value.favoriteProductIds.contains(productId)
            try {
                if (isFavorite) {
                    val favoriteId = _uiState.value.favoriteMap[productId]
                    if (favoriteId != null) {
                        foodRepository.deleteFavorite(favoriteId)
                        _uiState.update { state ->
                            state.copy(
                                favoriteProductIds = state.favoriteProductIds - productId,
                                favoriteMap = state.favoriteMap - productId
                            )
                        }
                    }
                } else {
                    val userId = sessionManager.getUserId()
                    if (userId == null) {
                        throw IllegalStateException("No hay usuario autenticado")
                    }
                    val newFav = foodRepository.addFavorite(userId, productId)
                    _uiState.update { state ->
                        state.copy(
                            favoriteProductIds = state.favoriteProductIds + productId,
                            favoriteMap = state.favoriteMap + (productId to newFav.id)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("CategoryVM", "Error toggle: ${e.message}")
            }
        }
    }
}
