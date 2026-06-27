package com.example.food_ucsc.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.state.RestaurantDetailUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantViewModel(
    private val foodRepository: FoodRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(RestaurantDetailUiState())
    val uiState: StateFlow<RestaurantDetailUiState> = _uiState.asStateFlow()

    fun loadRestaurantDetails(restaurantId: Int) {
        if (restaurantId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val restaurant = foodRepository.getRestaurantById(restaurantId)
                val menus = foodRepository.getMenusByRestaurant(restaurantId)
                
                val userId = sessionManager.getUserId()
                val favorites = if (userId != null) {
                    try {
                        foodRepository.getFavoritesByUser(userId)
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else {
                    try {
                        foodRepository.getMyFavoritesRaw()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                val favoriteProductIds = favorites.map { it.productoId }.toSet()
                val favoriteMap = favorites.associate { it.productoId to it.id }

                _uiState.update {
                    it.copy(
                        restaurant = restaurant,
                        menus = menus,
                        favoriteProductIds = favoriteProductIds,
                        favoriteMap = favoriteMap,
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                _uiState.update { it.copy(isLoading = false, error = ex.message) }
            }
        }
    }

    fun buyProduct(product: FoodItem) {
        viewModelScope.launch {
            try {
                // Registramos la compra en la API
                // Usamos el id del producto como identificador para la compra
                foodRepository.registerPurchase(product.id)
                
                // Calculamos los puntos ganados: precio / 100
                val puntosGanados = (product.precio_base / 100).toInt()
                
                _uiState.update { 
                    it.copy(purchaseMessage = "¡Compra exitosa! Has ganado $puntosGanados puntos.") 
                }
                
                // Opcional: Recargar datos del usuario para ver los puntos actualizados en el perfil
                // foodRepository.me() 

                // Limpiar el mensaje después de unos segundos
                delay(3000)
                _uiState.update { it.copy(purchaseMessage = null) }
                
            } catch (e: Exception) {
                Log.e("RestaurantVM", "Error al registrar compra: ${e.message}")
                _uiState.update { it.copy(error = "No se pudo registrar la compra") }
                delay(3000)
                _uiState.update { it.copy(error = null) }
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
                    val existingFavorite = runCatching { foodRepository.getFavoritesByUser(userId) }
                        .getOrDefault(emptyList())
                        .firstOrNull { it.productoId == productId }
                    if (existingFavorite != null) {
                        _uiState.update { state ->
                            state.copy(
                                favoriteProductIds = state.favoriteProductIds + productId,
                                favoriteMap = state.favoriteMap + (productId to existingFavorite.id)
                            )
                        }
                        return@launch
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
                Log.e("RestaurantVM", "Error al cambiar favorito: ${e.message}")
            }
        }
    }
}
