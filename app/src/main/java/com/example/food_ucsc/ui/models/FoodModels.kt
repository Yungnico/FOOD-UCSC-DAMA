package com.example.food_ucsc.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val name: String,
    val icon: ImageVector
)

data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val icon: ImageVector,
    val calories: Int = 0
)

data class NutritionalData(
    val date: String,
    val calories: Int
)
