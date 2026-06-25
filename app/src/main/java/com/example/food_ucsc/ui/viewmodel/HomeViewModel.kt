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

    init {
        loadHomeData()
        checkPendingRatings()
    }

    private fun loadHomeData() {
        // Evitar recargar si ya tenemos datos cargados
        if (_uiState.value.categories.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                val restaurants = foodRepository.getRestaurants()
                val menus = restaurants.flatMap { restaurant ->
                    runCatching { foodRepository.getMenusByRestaurant(restaurant.id) }
                        .getOrDefault(emptyList())
                }

                // Llamada a la API a través del repositorio para obtener las categorías
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

    private fun checkPendingRatings() {
        viewModelScope.launch {
            delay(5000)
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
