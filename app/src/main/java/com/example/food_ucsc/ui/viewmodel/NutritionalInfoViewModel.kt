package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.food_ucsc.ui.models.NutritionalData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NutritionalInfoViewModel : ViewModel() {
    private val _dailyCalories = MutableStateFlow(1850)
    val dailyCalories: StateFlow<Int> = _dailyCalories.asStateFlow()

    private val _weeklyData = MutableStateFlow(listOf(
        NutritionalData("Lun", 2100),
        NutritionalData("Mar", 1950),
        NutritionalData("Mié", 2300),
        NutritionalData("Jue", 1800),
        NutritionalData("Vie", 2400),
        NutritionalData("Sáb", 2000),
        NutritionalData("Dom", 1850)
    ))
    val weeklyData: StateFlow<List<NutritionalData>> = _weeklyData.asStateFlow()

    val calorieGoal = 2200
}
