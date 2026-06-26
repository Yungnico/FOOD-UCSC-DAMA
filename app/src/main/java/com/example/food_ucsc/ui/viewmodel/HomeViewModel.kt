package com.example.food_ucsc.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.state.HomeUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allProducts: List<FoodItem> = emptyList()
    private var waterReminderJob: Job? = null

    private val waterPhrases = listOf(
        "¡Hidrátate! Tu cuerpo te lo agradecerá.",
        "Un vaso de agua es un paso más hacia una vida saludable.",
        "Beber agua mejora tu concentración y energía.",
        "No esperes a tener sed, ¡bebe agua ahora!",
        "Mantén tu mente fresca y tu cuerpo hidratado.",
        "El agua es el combustible de tu bienestar."
    )

    init {
        loadHomeData()
        checkPendingRatings()
        startWaterReminder()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Carga de Categorías y Productos base
                val categories = runCatching { foodRepository.getCategories() }.getOrDefault(emptyList())
                val baseProducts = runCatching { foodRepository.getProducts() }.getOrDefault(emptyList())

                // 2. Carga de Locales y sus Menús para obtener todos los productos posibles
                val restaurants = runCatching { foodRepository.getRestaurants() }.getOrDefault(emptyList())
                val menus = restaurants.flatMap { restaurant ->
                    runCatching { foodRepository.getMenusByRestaurant(restaurant.id) }.getOrDefault(emptyList())
                }
                val menuProducts = menus.flatMap { it.productos }

                // 3. Sincronización de productos conocidos (Base + Menús)
                allProducts = (foodRepository.getProducts() + menuProducts).distinctBy { it.id }

                val categories = runCatching { foodRepository.getCategories() }
                    .getOrDefault(emptyList())

                // 4. Carga de Favoritos
                val favoritesRaw = try { foodRepository.getMyFavoritesRaw() } catch (e: Exception) { emptyList() }
                val favIds = favoritesRaw.map { it.productoId }.toSet()
                val favMap = favoritesRaw.associate { it.productoId to it.id }
                
                // Construir la lista de items favoritos usando los IDs y los productos conocidos
                val favoriteItems = allProducts.filter { favIds.contains(it.id) }

                val recommended = if (menuProducts.isNotEmpty()) menuProducts.distinctBy { it.id }.take(6) else allProducts.take(6)
                val tips = runCatching { foodRepository.getTips() }.getOrDefault(emptyList())
                val challenges = runCatching { foodRepository.getChallenges() }.getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        categories = categories,
                        recommendedItems = recommended,
                        favoriteItems = favoriteItems,
                        favoriteProductIds = favIds,
                        favoriteMap = favMap,
                        tips = tips,
                        challenges = challenges,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                Log.e("HomeVM", "Error general: ${e.message}")
                _uiState.update {
                    it.copy(
                        categories = emptyList(),
                        recommendedItems = emptyList(),
                        favoriteItems = emptyList(),
                        isLoading = false,
                        error = "Error de conexión"
                    )
                }
            }
            }
        }
    }

            }
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isFavorite = currentState.favoriteProductIds.contains(productId)
            
            try {
                if (isFavorite) {
                    val favoriteId = currentState.favoriteMap[productId]
                    // Actualización optimista: Quitar de la UI inmediatamente
                    _uiState.update { state ->
                        state.copy(
                            favoriteProductIds = state.favoriteProductIds - productId,
                            favoriteItems = state.favoriteItems.filter { it.id != productId },
                            favoriteMap = state.favoriteMap - productId
                        )
                    }
                    if (favoriteId != null) {
                        foodRepository.deleteFavorite(favoriteId)
                    }
                } else {
                    // Actualización optimista: Agregar a la UI inmediatamente
                    val productToAdd = allProducts.find { it.id == productId }
                    _uiState.update { state ->
                        state.copy(
                            favoriteProductIds = state.favoriteProductIds + productId,
                            favoriteItems = if (productToAdd != null) state.favoriteItems + productToAdd else state.favoriteItems
                        )
                    }
                    val newFav = foodRepository.addFavorite(productId)
                    // Sincronizar el ID real del favorito devuelto por la API
                    _uiState.update { state ->
                        state.copy(favoriteMap = state.favoriteMap + (productId to newFav.id))
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Error en favorito: ${e.message}")
                // En caso de error crítico, re-sincronizar todo desde el servidor
                loadHomeData()
            }
        }
    }
            }
        }
    }

    fun dismissWaterReminder() {
        _uiState.update { it.copy(showWaterReminder = false) }
    }

    fun setWaterReminderInterval(minutes: Int) {
        _uiState.update { it.copy(waterReminderIntervalMinutes = minutes) }
        startWaterReminder() // Reinicia el contador con el nuevo tiempo
    }

    fun dismissRatingDialog() {
        _uiState.update { it.copy(showRatingDialog = false) }
    }

    fun toggleFilterSheet(show: Boolean) {
        _uiState.update { it.copy(showFilterSheet = show) }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { state ->
            val newState = state.copy(selectedNutritionalFilter = filter, showFilterSheet = false)
            applyFiltersToState(newState)
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersToState()
    }

    private fun applyFiltersToState() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty() && _uiState.value.selectedNutritionalFilter == "Todos") {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        val results = allProducts.filter { it.nombre.contains(query, ignoreCase = true) }
        _uiState.update { it.copy(searchResults = results) }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedNutritionalFilter = filter, showFilterSheet = false) }
        applyFiltersToState()
    }

    fun toggleFilterSheet(show: Boolean) { _uiState.update { it.copy(showFilterSheet = show) } }
    fun dismissRatingDialog() { _uiState.update { it.copy(showRatingDialog = false) } }
    private fun checkPendingRatings() {
        viewModelScope.launch {
            delay(5000)
            _uiState.update { it.copy(showRatingDialog = true, pendingOrderId = "101") }
        }
    }
}
