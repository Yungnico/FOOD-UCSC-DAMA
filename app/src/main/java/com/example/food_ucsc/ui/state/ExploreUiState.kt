package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Restaurant

data class PriceInfo(
    val restaurantName: String,
    val price: Double,
    val productId: Int
)

data class ComparisonItem(
    val productName: String,
    val prices: List<PriceInfo>
)

data class ExploreUiState(
    val restaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComparing: Boolean = false,
    val comparisonResults: List<ComparisonItem> = emptyList(),
    val purchaseMessage: String? = null
)
