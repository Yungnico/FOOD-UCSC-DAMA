package com.example.food_ucsc.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val name: String,
    val icon: ImageVector
)

data class FoodItem(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio_base: Double,
    val categoria_basica: String,
    val stock: Int,
    val icon: ImageVector,
    val calories: Int = 0
)

data class Menu(
    val id: Int,
    val local_id: Int,
    val fecha: String,
    val titulo: String,
    val promociones: String,
    val productos: List<FoodItem> = emptyList()
)

data class NutritionalData(
    val date: String,
    val calories: Int
)

data class HealthTip(
    val id: Int,
    val descripcion: String,
    val categoria: String
)

data class Challenge(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val recompensaPuntos: Int
)

data class Restaurant(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val horario: String,
    val contacto: String,
    val latitude: Double,
    val longitude: Double,
    val tiempo_espera_estimado: String,
    val rating: Double,
    val icon: ImageVector,
    val bannerColor: Long = 0xFF6750A4
)
