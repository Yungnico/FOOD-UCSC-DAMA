package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.state.NutritionalInfoUiState
import com.example.food_ucsc.ui.state.NutritionalPurchaseItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NutritionalInfoViewModel(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionalInfoUiState())
    val uiState: StateFlow<NutritionalInfoUiState> = _uiState.asStateFlow()

    init {
        loadNutritionSummary()
    }

    private fun loadNutritionSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.getNutritionSummary()
            }.onSuccess { summary ->
                val weeklyData = summary.weeklyData.map { day ->
                    com.example.food_ucsc.ui.models.NutritionalData(
                        date = day.date,
                        calories = day.calories
                    )
                }

                val purchasedItems = summary.purchasedItems.map { purchase ->
                    NutritionalPurchaseItem(
                        name = purchase.name,
                        calories = purchase.calories
                    )
                }

                _uiState.update {
                    it.copy(
                        dailyCalories = summary.dailyCalories,
                        calorieGoal = summary.calorieGoal,
                        weeklyData = weeklyData,
                        purchasedItems = purchasedItems,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = ex.message ?: "No se pudo cargar la información nutricional"
                    )
                }
            }
        }
    }
}
