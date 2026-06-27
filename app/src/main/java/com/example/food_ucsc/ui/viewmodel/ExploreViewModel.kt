package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.state.ComparisonItem
import com.example.food_ucsc.ui.state.ExploreUiState
import com.example.food_ucsc.ui.state.PriceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadRestaurants()
    }

    private fun loadRestaurants() {
        if (_uiState.value.restaurants.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                foodRepository.getRestaurants()
            }.onSuccess { restaurants ->
                _uiState.update {
                    it.copy(
                        restaurants = restaurants,
                        filteredRestaurants = restaurants,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        restaurants = emptyList(),
                        filteredRestaurants = emptyList(),
                        isLoading = false,
                        error = ex.message ?: "No se pudieron cargar los locales"
                    )
                }
            }
        }
    }

    fun toggleComparison() {
        val nextComparing = !_uiState.value.isComparing
        _uiState.update { it.copy(isComparing = nextComparing) }
        
        if (nextComparing && _uiState.value.comparisonResults.isEmpty()) {
            performComparison()
        }
    }

    private fun performComparison() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Obtenemos todos los detalles de productos
                val productsDto = foodRepository.getProductDetails()
                val restaurants = _uiState.value.restaurants
                
                // Agrupamos por nombre de producto (normalizado)
                val comparison = productsDto.groupBy { it.nombre.trim().lowercase() }
                    .filter { it.value.size > 1 } // Solo los que están en más de un sitio
                    .map { (name, dtos) ->
                        val prices = dtos.flatMap { dto ->
                            dto.menus.mapNotNull { menu ->
                                val restName = restaurants.find { it.id == menu.localId }?.nombre
                                if (restName != null) {
                                    PriceInfo(restaurantName = restName, price = dto.precioBase)
                                } else null
                            }
                        }.distinctBy { it.restaurantName }
                        
                        ComparisonItem(
                            productName = dtos.first().nombre,
                            prices = prices.sortedBy { it.price }
                        )
                    }
                    .filter { it.prices.size > 1 }
                
                _uiState.update { 
                    it.copy(
                        comparisonResults = comparison,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al comparar precios: ${e.message}"
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filtered = filterRestaurants(state.restaurants, query, state.selectedFilter)
            state.copy(searchQuery = query, filteredRestaurants = filtered)
        }
    }

    fun onFilterChange(filter: String) {
        _uiState.update { state ->
            val filtered = filterRestaurants(state.restaurants, state.searchQuery, filter)
            state.copy(selectedFilter = filter, filteredRestaurants = filtered)
        }
    }

    private fun filterRestaurants(
        restaurants: List<com.example.food_ucsc.ui.models.Restaurant>,
        query: String,
        filter: String
    ): List<com.example.food_ucsc.ui.models.Restaurant> {
        val normalizedQuery = query.trim().lowercase()

        return restaurants.filter { restaurant ->
            val matchesQuery = normalizedQuery.isBlank() ||
                restaurant.nombre.lowercase().contains(normalizedQuery) ||
                restaurant.descripcion.lowercase().contains(normalizedQuery)

            val matchesFilter = when (filter) {
                "Todos" -> true
                "Rápidos" -> restaurant.tiempo_espera_estimado <= 10
                "Populares" -> restaurant.rating >= 4.5
                "Cerca" -> restaurant.tiempo_espera_estimado <= 15
                "Abierto ahora" -> true
                else -> true
            }

            matchesQuery && matchesFilter
        }.sortedWith(
            when (filter) {
                "Rápidos" -> compareBy { it.tiempo_espera_estimado }
                "Populares" -> compareByDescending<com.example.food_ucsc.ui.models.Restaurant> { it.rating }
                else -> compareBy { it.nombre }
            }
        )
    }
}
