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
    @SerializedName("tiempo_espera_estimado") val tiempo_espera_estimado: String,
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

data class FavoriteDto(
    @SerializedName("id") val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("producto_id") val productoId: Int,
    @SerializedName("producto") val producto: FoodItemDto? = null
)

data class TipDto(
    @SerializedName("id") val id: Int,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoria") val categoria: String
)

data class ChallengeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("recompensa_puntos") val recompensaPuntos: Int
)
