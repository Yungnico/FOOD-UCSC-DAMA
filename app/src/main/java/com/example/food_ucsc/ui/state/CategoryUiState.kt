package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.Restaurant

data class CategoryUiState(
    val categoryName: String = "",
    val items: List<FoodItem> = emptyList(),
    val restaurants: List<Restaurant> = emptyList(),
    val categories: List<Category> = emptyList(),
    val favoriteProductIds: Set<Int> = emptySet(),
    val favoriteMap: Map<Int, Int> = emptyMap(), // productId to favoriteId
    val isLoading: Boolean = false,
    val error: String? = null
)
