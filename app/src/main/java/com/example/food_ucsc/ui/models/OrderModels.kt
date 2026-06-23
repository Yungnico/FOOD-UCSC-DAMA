package com.example.food_ucsc.ui.models

data class Order(
    val id: String,
    val restaurantName: String,
    val date: String,
    val total: Double,
    val status: String, // e.g., "Entregado", "En camino"
    val items: List<String>,
    val isRated: Boolean = false
)
