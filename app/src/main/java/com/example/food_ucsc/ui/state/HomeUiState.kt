package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val recommendedItems: List<FoodItem> = emptyList(),
    val favoriteItems: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)
