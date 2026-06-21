package com.example.food_ucsc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RestaurantDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("contacto") val contacto: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("tiempo_espera_estatico") val tiempoEspera: String,
    @SerializedName("rating") val rating: Double
)

data class MenuDto(
    @SerializedName("id") val id: Int,
    @SerializedName("local_id") val localId: Int,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("promociones") val promociones: String,
    @SerializedName("productos") val productos: List<FoodItemDto> = emptyList()
)

data class FoodItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("precio_base") val precioBase: Double,
    @SerializedName("categoria_basica") val categoriaBasica: String,
    @SerializedName("stock") val stock: Int
)
