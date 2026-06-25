package com.example.food_ucsc.data.remote.service

import com.example.food_ucsc.data.remote.dto.AuthResponseDto
import com.example.food_ucsc.data.remote.dto.CategoryDto
import com.example.food_ucsc.data.remote.dto.ChallengeDto
import com.example.food_ucsc.data.remote.dto.FavoriteDto
import com.example.food_ucsc.data.remote.dto.LoginRequestDto
import com.example.food_ucsc.data.remote.dto.MenuDto
import com.example.food_ucsc.data.remote.dto.RegisterRequestDto
import com.example.food_ucsc.data.remote.dto.ProductDetailDto
import com.example.food_ucsc.data.remote.dto.PurchaseDto
import com.example.food_ucsc.data.remote.dto.PurchaseRegistrationRequestDto
import com.example.food_ucsc.data.remote.dto.PurchaseRatingUpdateDto
import com.example.food_ucsc.data.remote.dto.NutritionSummaryDto
import com.example.food_ucsc.data.remote.dto.RestaurantDto
import com.example.food_ucsc.data.remote.dto.SimpleMessageDto
import com.example.food_ucsc.data.remote.dto.TipDto
import com.example.food_ucsc.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("locales")
    suspend fun getRestaurants(): List<RestaurantDto>

    @GET("locales/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): RestaurantDto

    @GET("locales/{id}/menus")
    suspend fun getMenusByRestaurant(@Path("id") restaurantId: Int): List<MenuDto>

    @GET("categorias-comida")
    suspend fun getCategories(): List<CategoryDto>

    @GET("productos")
    suspend fun getProducts(): List<ProductDetailDto>

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @GET("me")
    suspend fun me(): UserDto

    @GET("me/resumen-nutricional")
    suspend fun getNutritionSummary(): NutritionSummaryDto

    @POST("logout")
    suspend fun logout(): SimpleMessageDto

    @GET("compras")
    suspend fun getMyPurchases(): List<PurchaseDto>

    @POST("compras/registrar")
    suspend fun registerPurchase(
        @Body request: PurchaseRegistrationRequestDto
    ): Map<String, Any>

    @PUT("compras/{id}")
    suspend fun updatePurchaseRating(
        @Path("id") purchaseId: Int,
        @Body request: PurchaseRatingUpdateDto
    ): PurchaseDto

    @GET("usuarios/{id}/favoritos")
    suspend fun getFavoritesByUser(@Path("id") userId: Int): List<FavoriteDto>

    @GET("favoritos/mios")
    suspend fun getMyFavorites(): List<FavoriteDto>

    @GET("consejos")
    suspend fun getTips(): List<TipDto>

    @GET("desafios")
    suspend fun getChallenges(): List<ChallengeDto>
}
