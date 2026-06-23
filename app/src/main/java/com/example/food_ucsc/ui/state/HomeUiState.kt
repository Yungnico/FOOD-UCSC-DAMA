package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val recommendedItems: List<FoodItem> = emptyList(),
    val favoriteItems: List<FoodItem> = emptyList(),
    val tips: List<HealthTip> = emptyList(),
    val challenges: List<Challenge> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val showRatingDialog: Boolean = false,
    val pendingOrderId: String? = null
)
