package com.example.food_ucsc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String? = null,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("objetivos_salud") val objetivosSalud: String? = null,
    @SerializedName("calorias_target") val caloriasTarget: Int? = null
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String,
    @SerializedName("email") val email: String,
    @SerializedName("objetivos_salud") val objetivosSalud: String? = null,
    @SerializedName("calorias_target") val caloriasTarget: Int = 0,
    @SerializedName("puntos") val puntos: Int = 0
)

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user") val user: UserDto
)

data class NutritionSummaryDayDto(
    @SerializedName("date") val date: String,
    @SerializedName("calories") val calories: Int
)

data class NutritionSummaryItemDto(
    @SerializedName("name") val name: String,
    @SerializedName("calories") val calories: Int,
    @SerializedName("local") val local: String? = null,
    @SerializedName("date") val date: String? = null
)

data class NutritionSummaryDto(
    @SerializedName("daily_calories") val dailyCalories: Int,
    @SerializedName("calorie_goal") val calorieGoal: Int,
    @SerializedName("weekly_data") val weeklyData: List<NutritionSummaryDayDto> = emptyList(),
    @SerializedName("purchased_items") val purchasedItems: List<NutritionSummaryItemDto> = emptyList()
)

data class SimpleMessageDto(
    @SerializedName("message") val message: String
)

data class RestaurantDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("contacto") val contacto: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("tiempo_espera_estimado") val tiempo_espera_estimado: Int,
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

data class LocalSummaryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

data class ProductSummaryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName(value = "informacion_nutricional", alternate = ["informacionNutricional"]) val informacionNutricional: NutritionDetailDto? = null
)

data class NutritionDetailDto(
    @SerializedName("calorias") val calorias: Int = 0,
    @SerializedName("proteina") val proteina: Double = 0.0,
    @SerializedName("carbohidratos") val carbohidratos: Double = 0.0,
    @SerializedName("grasas") val grasas: Double = 0.0,
    @SerializedName("sodio") val sodio: Double = 0.0,
    @SerializedName("puntaje") val puntaje: Double = 0.0
)

data class MenuSummaryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("local_id") val localId: Int,
    @SerializedName("titulo") val titulo: String? = null,
    @SerializedName("local") val local: LocalSummaryDto? = null
)

data class MenuProductSummaryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("menu_id") val menuId: Int,
    @SerializedName("producto_id") val productoId: Int,
    @SerializedName("precio_venta") val precioVenta: Double,
    @SerializedName("disponible") val disponible: Boolean,
    @SerializedName("menu") val menu: MenuSummaryDto? = null,
    @SerializedName("producto") val producto: ProductSummaryDto? = null
)

data class PurchaseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("menu_producto_id") val menuProductoId: Int,
    @SerializedName("fecha_compra") val fechaCompra: String,
    @SerializedName("calificacion") val calificacion: Int? = null,
    @SerializedName(value = "menu_producto", alternate = ["menuProducto"]) val menuProducto: MenuProductSummaryDto? = null
)

data class PurchaseRegistrationRequestDto(
    @SerializedName("menu_producto_id") val menuProductoId: Int,
    @SerializedName("calificacion") val calificacion: Int? = null
)

data class PurchaseRatingUpdateDto(
    @SerializedName("calificacion") val calificacion: Int
)

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

data class ProductMenuDto(
    @SerializedName("id") val id: Int,
    @SerializedName("local_id") val localId: Int,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("promociones") val promociones: String
)

data class ProductDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoria_basica") val categoriaBasica: String,
    @SerializedName("stock") val stock: Int,
    @SerializedName("precio_base") val precioBase: Double,
    @SerializedName("menus") val menus: List<ProductMenuDto> = emptyList(),
    @SerializedName("categorias") val categorias: List<CategoryDto> = emptyList()
)
