package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip
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

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                val restaurants = foodRepository.getRestaurants()
                val menus = restaurants.flatMap { restaurant ->
                    runCatching { foodRepository.getMenusByRestaurant(restaurant.id) }
                        .getOrDefault(emptyList())
                }

                allProducts = foodRepository.getProducts()

                val categories = runCatching { foodRepository.getCategories() }
                    .getOrDefault(emptyList())

                val recommended = menus
                    .flatMap { it.productos }
                    .distinctBy { it.id }
                    .take(6)

                val favorites = sessionManager.getUserId()?.let { userId ->
                    runCatching { foodRepository.getFavoritesByUser(userId) }
                        .getOrDefault(emptyList())
                }.orEmpty()

                val tips = runCatching { foodRepository.getTips() }
                    .getOrDefault(emptyList())

                val challenges = runCatching { foodRepository.getChallenges() }
                    .getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        categories = categories,
                        recommendedItems = recommended,
                        favoriteItems = favorites,
                        tips = tips.ifEmpty {
                            listOf(
                                HealthTip(1, "Mantén una hidratación constante durante el día.", "hidratacion"),
                                HealthTip(2, "Agrega verduras a tu almuerzo para mejorar el balance.", "balance")
                            )
                        },
                        challenges = challenges,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        categories = emptyList(),
                        recommendedItems = emptyList(),
                        favoriteItems = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun checkPendingRatings() {
        viewModelScope.launch {
            delay(3000)
            runCatching {
                val purchases = foodRepository.getMyPurchases()
                val pendingOrder = purchases.firstOrNull { !it.isRated }
                
                if (pendingOrder != null) {
                    _uiState.update {
                        it.copy(
                            showRatingDialog = true,
                            pendingOrderId = pendingOrder.id
                        )
                    }
                }
            }.onFailure { }
        }
    }

    fun startWaterReminder() {
        waterReminderJob?.cancel()
        waterReminderJob = viewModelScope.launch {
            while (true) {
                delay(_uiState.value.waterReminderIntervalMinutes * 60 * 1000L)
                _uiState.update { 
                    it.copy(
                        showWaterReminder = true,
                        waterReminderPhrase = waterPhrases.random()
                    ) 
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
        _uiState.update { state ->
            val newState = state.copy(searchQuery = query)
            applyFiltersToState(newState)
        }
    }

    private fun applyFiltersToState(state: HomeUiState): HomeUiState {
        val query = state.searchQuery
        val filter = state.selectedNutritionalFilter

        if (query.isBlank() && filter == "Todos") {
            return state.copy(searchResults = emptyList())
        }

        val results = allProducts.filter { item ->
            val matchesQuery = query.isBlank() || 
                item.nombre.contains(query, ignoreCase = true) || 
                item.descripcion.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Bajo en calorías" -> item.calories in 1..400 || item.descripcion.lowercase().contains("bajo en cal")
                "Proteico" -> item.descripcion.lowercase().contains("prote") || item.nombre.lowercase().contains("prote")
                "Saludable" -> item.categoria_basica.equals("Saludable", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
        return state.copy(searchResults = results)
    }
}
