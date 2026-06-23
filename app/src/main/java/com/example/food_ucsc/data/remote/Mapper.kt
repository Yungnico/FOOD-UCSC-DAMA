package com.example.food_ucsc.data.remote

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Restaurant
import com.example.food_ucsc.data.remote.dto.ChallengeDto
import com.example.food_ucsc.data.remote.dto.FavoriteDto
import com.example.food_ucsc.data.remote.dto.FoodItemDto
import com.example.food_ucsc.data.remote.dto.MenuDto
import com.example.food_ucsc.data.remote.dto.RestaurantDto
import com.example.food_ucsc.data.remote.dto.TipDto
import com.example.food_ucsc.data.remote.dto.UserDto
import com.example.food_ucsc.ui.models.AppUser
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.models.Menu
import com.example.food_ucsc.ui.models.Restaurant

fun RestaurantDto.toDomain(): Restaurant {
    return Restaurant(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        horario = this.horario,
        contacto = this.contacto,
        latitude = this.latitude,
        longitude = this.longitude,
        tiempo_espera_estimado = this.tiempo_espera_estimado,
        rating = this.rating,
        icon = Icons.Default.Restaurant, // Default icon
        bannerColor = 0xFF6750A4 // Default color
    )
}

fun MenuDto.toDomain(): Menu {
    return Menu(
        id = this.id,
        local_id = this.localId,
        fecha = this.fecha,
        titulo = this.titulo,
        promociones = this.promociones,
        productos = this.productos.map { it.toDomain() }
    )
}

fun FoodItemDto.toDomain(): FoodItem {
    return FoodItem(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio_base = this.precioBase,
        categoria_basica = this.categoriaBasica,
        stock = this.stock,
        icon = Icons.Default.Fastfood // Default icon
    )
}

fun FavoriteDto.toDomainOrNull(): FoodItem? {
    return this.producto?.toDomain()
}

fun TipDto.toDomain(): HealthTip {
    return HealthTip(
        id = this.id,
        descripcion = this.descripcion,
        categoria = this.categoria
    )
}

fun ChallengeDto.toDomain(): Challenge {
    return Challenge(
        id = this.id,
        titulo = this.titulo,
        descripcion = this.descripcion,
        recompensaPuntos = this.recompensaPuntos
    )
}

fun UserDto.toDomain(): AppUser {
    return AppUser(
        id = this.id,
        nombre = this.nombre,
        apellidoPaterno = this.apellidoPaterno,
        apellidoMaterno = this.apellidoMaterno,
        email = this.email,
        objetivosSalud = this.objetivosSalud,
        caloriasTarget = this.caloriasTarget,
        puntos = this.puntos
    )
}
