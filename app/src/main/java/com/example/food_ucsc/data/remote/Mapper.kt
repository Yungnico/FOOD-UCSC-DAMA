package com.example.food_ucsc.data.remote

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Restaurant
import com.example.food_ucsc.data.remote.dto.CategoryDto
import com.example.food_ucsc.data.remote.dto.ChallengeDto
import com.example.food_ucsc.data.remote.dto.FavoriteDto
import com.example.food_ucsc.data.remote.dto.FoodItemDto
import com.example.food_ucsc.data.remote.dto.MenuDto
import com.example.food_ucsc.data.remote.dto.MenuProductSummaryDto
import com.example.food_ucsc.data.remote.dto.NutritionSummaryDto
import com.example.food_ucsc.data.remote.dto.PurchaseDto
import com.example.food_ucsc.data.remote.dto.ProductDetailDto
import com.example.food_ucsc.data.remote.dto.ProductMenuDto
import com.example.food_ucsc.data.remote.dto.RestaurantDto
import com.example.food_ucsc.data.remote.dto.TipDto
import com.example.food_ucsc.data.remote.dto.UserDto
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.AppUser
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.models.Menu
import com.example.food_ucsc.ui.models.Order
import com.example.food_ucsc.ui.models.Restaurant
import java.text.SimpleDateFormat
import java.util.Locale

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

fun CategoryDto.toDomain(): Category {
    return Category(
        name = this.nombre,
        icon = when (this.nombre.lowercase()) {
            "económica" -> Icons.Default.MoreHoriz
            "vegetariana" -> Icons.Default.Grass
            "vegana" -> Icons.Default.Eco
            "casera" -> Icons.Default.Home
            "sin gluten" -> Icons.Default.BakeryDining
            "saludable" -> Icons.Default.Restaurant
            "comida rápida" -> Icons.Default.Fastfood
            "postres" -> Icons.Default.Cake
            "bebidas" -> Icons.Default.Coffee
            else -> Icons.Default.Restaurant
        }
    )
}

fun ProductDetailDto.toFoodItem(): FoodItem {
    return FoodItem(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio_base = this.precioBase,
        categoria_basica = this.categoriaBasica,
        stock = this.stock,
        icon = when (this.categoriaBasica.lowercase()) {
            "vegetariana" -> Icons.Default.Grass
            "vegana" -> Icons.Default.Eco
            "casera" -> Icons.Default.Home
            "sin gluten" -> Icons.Default.BakeryDining
            "postres" -> Icons.Default.Cake
            "bebidas" -> Icons.Default.Coffee
            "comida rápida" -> Icons.Default.Fastfood
            else -> Icons.Default.Restaurant
        }
    )
}

fun ProductMenuDto.toMenuItem() = this

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

fun PurchaseDto.toOrder(): Order {
    val localName = this.menuProducto?.menu?.local?.nombre ?: "Local"
    val productName = this.menuProducto?.producto?.nombre ?: "Producto"
    val formattedDate = runCatching {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        output.format(input.parse(this.fechaCompra.replace("T", " ").substringBefore(".") )!!)
    }.getOrDefault(this.fechaCompra)

    return Order(
        id = this.id.toString(),
        restaurantName = localName,
        date = formattedDate,
        total = this.menuProducto?.precioVenta ?: 0.0,
        status = if (this.calificacion != null) "Calificado" else "Entregado",
        items = listOf(productName),
        isRated = this.calificacion != null
    )
}

fun PurchaseDto.caloriesOrZero(): Int {
    return this.menuProducto?.producto?.informacionNutricional?.calorias ?: 0
}
