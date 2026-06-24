package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.NutritionalData

data class NutritionalPurchaseItem(
    val name: String,
    val calories: Int
)

data class NutritionalInfoUiState(
    val dailyCalories: Int = 0,
    val calorieGoal: Int = 2200,
    val weeklyData: List<NutritionalData> = emptyList(),
    val purchasedItems: List<NutritionalPurchaseItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)