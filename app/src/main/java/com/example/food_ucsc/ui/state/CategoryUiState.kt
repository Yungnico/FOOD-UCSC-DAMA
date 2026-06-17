package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.FoodItem

data class CategoryUiState(
    val categoryName: String = "",
    val items: List<FoodItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
