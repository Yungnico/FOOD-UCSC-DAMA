package com.example.food_ucsc.data.remote.service

import com.example.food_ucsc.data.remote.dto.*
import retrofit2.http.*

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

    @POST("favoritos")
    suspend fun addFavorite(@Body request: Map<String, Int>): FavoriteDto

    @DELETE("favoritos/{id}")
    suspend fun deleteFavorite(@Path("id") favoriteId: Int): SimpleMessageDto

    @GET("consejos")
    suspend fun getTips(): List<TipDto>

    @GET("desafios")
    suspend fun getChallenges(): List<ChallengeDto>
}
