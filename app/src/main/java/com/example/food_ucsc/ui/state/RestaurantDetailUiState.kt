package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Menu
import com.example.food_ucsc.ui.models.Restaurant

data class RestaurantDetailUiState(
    val restaurant: Restaurant? = null,
    val menus: List<Menu> = emptyList(),
    val favoriteProductIds: Set<Int> = emptySet(),
    val favoriteMap: Map<Int, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val purchaseMessage: String? = null
)
