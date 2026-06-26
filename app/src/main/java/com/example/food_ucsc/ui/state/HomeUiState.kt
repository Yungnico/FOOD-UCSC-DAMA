package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val recommendedItems: List<FoodItem> = emptyList(),
    val favoriteItems: List<FoodItem> = emptyList(),
    val favoriteProductIds: Set<Int> = emptySet(),
    val favoriteMap: Map<Int, Int> = emptyMap(), // Mapeo de ProductoID -> FavoritoID
    val searchResults: List<FoodItem> = emptyList(),
    val tips: List<HealthTip> = emptyList(),
    val challenges: List<Challenge> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedNutritionalFilter: String = "Todos",
    val showRatingDialog: Boolean = false,
    val pendingOrderId: String? = null,
    val showFilterSheet: Boolean = false,
    val showFilterSheet: Boolean = false,
    
    // Funcionalidad de recordatorio de agua
    val showWaterReminder: Boolean = false,
    val waterReminderPhrase: String = "",
    val waterReminderIntervalMinutes: Int = 1,
    val error: String? = null
)
