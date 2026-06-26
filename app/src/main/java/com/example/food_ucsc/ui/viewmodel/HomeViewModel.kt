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
                // 1. Cargar Categorías y Productos base
                val categories = runCatching { foodRepository.getCategories() }.getOrDefault(emptyList())
                val baseProducts = runCatching { foodRepository.getProducts() }.getOrDefault(emptyList())

                // 2. Cargar Locales y Menús (para tener el catálogo completo)
                val restaurants = runCatching { foodRepository.getRestaurants() }.getOrDefault(emptyList())
                val menus = restaurants.flatMap { restaurant ->
                    runCatching { foodRepository.getMenusByRestaurant(restaurant.id) }.getOrDefault(emptyList())
                }
                val menuProducts = menus.flatMap { it.productos }
                allProducts = (baseProducts + menuProducts).distinctBy { it.id }

                // 3. NUEVA LÓGICA: Cargar productos en TENDENCIA desde el servidor
                Log.d("HomeVM", "Iniciando carga de tendencias...")
                val trendingResult = runCatching { foodRepository.getTrendingProducts() }
                
                val recommendedItems = if (trendingResult.isSuccess) {
                    val items = trendingResult.getOrNull() ?: emptyList()
                    Log.d("HomeVM", "Tendencias cargadas exitosamente: ${items.size} productos")
                    if (items.isEmpty()) {
                        Log.d("HomeVM", "Sin tendencias disponibles, usando backup de 10 productos")
                        allProducts.take(10)
                    } else {
                        items
                    }
                } else {
                    Log.e("HomeVM", "Error al cargar tendencias: ${trendingResult.exceptionOrNull()?.message}")
                    Log.d("HomeVM", "Usando backup de 10 productos por error en servidor")
                    allProducts.take(10)
                }

                // 4. Sincronizar Favoritos del usuario actual
                val userId = sessionManager.getUserId()
                val favoritesRaw = if (userId != null) {
                    runCatching { foodRepository.getFavoritesByUser(userId) }.getOrDefault(emptyList())
                } else {
                    runCatching { foodRepository.getMyFavoritesRaw() }.getOrDefault(emptyList())
                }
                
                val favoriteProductIds = favoritesRaw.map { it.productoId }.toSet()
                val favoriteMap = favoritesRaw.associate { it.productoId to it.id }
                val favoriteItems = allProducts.filter { it.id in favoriteProductIds }

                val tips = runCatching { foodRepository.getTips() }.getOrDefault(emptyList())
                val challenges = runCatching { foodRepository.getChallenges() }.getOrDefault(emptyList())

                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        recommendedItems = recommendedItems,
                        favoriteItems = favoriteItems,
                        favoriteProductIds = favoriteProductIds,
                        favoriteMap = favoriteMap,
                        tips = tips,
                        challenges = challenges,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                Log.e("HomeVM", "Error en loadHomeData: ${e.message}")
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión") }
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
                    val userId = sessionManager.getUserId() ?: throw IllegalStateException("No hay usuario")
                    
                    val productToAdd = allProducts.find { it.id == productId }
                    _uiState.update { state ->
                        state.copy(
                            favoriteProductIds = state.favoriteProductIds + productId,
                            favoriteItems = if (productToAdd != null) state.favoriteItems + productToAdd else state.favoriteItems
                        )
                    }
                    val newFavorite = foodRepository.addFavorite(userId, productId)
                    _uiState.update { state ->
                        state.copy(favoriteMap = state.favoriteMap + (productId to newFavorite.id))
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Error en favorito: ${e.message}")
                loadHomeData() // Re-sincronizar
            }
        }
    }

    // Métodos de UI (Buscador, Filtros, Recordatorios)
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
    fun dismissWaterReminder() { _uiState.update { it.copy(showWaterReminder = false) } }
    fun setWaterReminderInterval(minutes: Int) {
        _uiState.update { it.copy(waterReminderIntervalMinutes = minutes) }
        startWaterReminder()
    }
    fun dismissRatingDialog() { _uiState.update { it.copy(showRatingDialog = false) } }
    private fun checkPendingRatings() {
        viewModelScope.launch {
            delay(5000)
            _uiState.update { it.copy(showRatingDialog = true, pendingOrderId = "101") }
        }
    }
    private fun startWaterReminder() {
        waterReminderJob?.cancel()
        waterReminderJob = viewModelScope.launch {
            while (true) {
                delay(_uiState.value.waterReminderIntervalMinutes * 60_000L)
                _uiState.update { it.copy(showWaterReminder = true, waterReminderPhrase = waterPhrases.random()) }
            }
        }
    }
}
