package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Restaurant

data class ExploreUiState(
    val restaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val isLoading: Boolean = false,
    val error: String? = null
)
