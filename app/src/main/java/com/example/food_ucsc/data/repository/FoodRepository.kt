package com.example.food_ucsc.data.repository

import android.util.Log
import com.example.food_ucsc.data.remote.*
import com.example.food_ucsc.data.remote.dto.*
import com.example.food_ucsc.data.remote.service.ApiService
import com.example.food_ucsc.ui.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodRepository(private val apiService: ApiService) {

    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        runCatching { apiService.getRestaurants().map { it.toDomain() } }.getOrDefault(emptyList())
    }

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        runCatching { apiService.getCategories().map { it.toDomain() } }.getOrDefault(emptyList())
    }

    suspend fun getProducts(): List<FoodItem> = withContext(Dispatchers.IO) {
        runCatching { apiService.getProducts().map { it.toFoodItem() } }.getOrDefault(emptyList())
    }

    // Quitamos el runCatching para que el ViewModel reciba el error real
    suspend fun getTrendingProducts(): List<FoodItem> = withContext(Dispatchers.IO) {
        apiService.getTrendingProducts().map { it.toFoodItem() }
    }

    suspend fun getProductDetails(): List<ProductDetailDto> = withContext(Dispatchers.IO) {
        runCatching { apiService.getProducts() }.getOrDefault(emptyList())
    }

    suspend fun getRestaurantById(id: Int): Restaurant = withContext(Dispatchers.IO) {
        apiService.getRestaurantById(id).toDomain()
    }

    suspend fun getMenusByRestaurant(restaurantId: Int): List<Menu> = withContext(Dispatchers.IO) {
        runCatching { apiService.getMenusByRestaurant(restaurantId).map { it.toDomain() } }.getOrDefault(emptyList())
    }

    suspend fun getMyFavorites(): List<FoodItem> = withContext(Dispatchers.IO) {
        try {
            apiService.getMyFavorites().mapNotNull { it.toDomainOrNull() }
        } catch (e: Exception) {
            Log.e("Repository", "Favoritos no disponibles: ${e.message}")
            emptyList()
        }
    }

    suspend fun getMyFavoritesRaw(): List<FavoriteDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getMyFavorites()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFavoritesByUser(userId: Int): List<FavoriteDto> = withContext(Dispatchers.IO) {
        try {
            apiService.getFavoritesByUser(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addFavorite(userId: Int, productId: Int): FavoriteDto = withContext(Dispatchers.IO) {
        apiService.addFavorite(mapOf("usuario_id" to userId, "producto_id" to productId))
    }

    suspend fun deleteFavorite(favoriteId: Int) = withContext(Dispatchers.IO) {
        apiService.deleteFavorite(favoriteId)
    }

    suspend fun login(email: String, password: String): Pair<String, AppUser> = withContext(Dispatchers.IO) {
        val response = apiService.login(LoginRequestDto(email = email, password = password))
        response.token to response.user.toDomain()
    }

    suspend fun register(nombre: String, apP: String, apM: String, email: String, pass: String): Pair<String, AppUser> = withContext(Dispatchers.IO) {
        val response = apiService.register(RegisterRequestDto(nombre, apP.ifBlank { null }, apM.ifBlank { null }, email, pass))
        response.token to response.user.toDomain()
    }

    suspend fun me(): AppUser = withContext(Dispatchers.IO) { apiService.me().toDomain() }
    suspend fun logout() = withContext(Dispatchers.IO) { apiService.logout() }
    suspend fun getMyPurchases(): List<Order> = withContext(Dispatchers.IO) { apiService.getMyPurchases().map { it.toOrder() } }
    suspend fun getTips(): List<HealthTip> = withContext(Dispatchers.IO) { apiService.getTips().map { it.toDomain() } }
    suspend fun getChallenges(): List<Challenge> = withContext(Dispatchers.IO) { apiService.getChallenges().map { it.toDomain() } }

    suspend fun getNutritionSummary(): NutritionSummaryDto = withContext(Dispatchers.IO) {
        apiService.getNutritionSummary()
    }

    suspend fun ratePurchase(purchaseId: Int, rating: Int): PurchaseDto = withContext(Dispatchers.IO) {
        apiService.updatePurchaseRating(purchaseId, PurchaseRatingUpdateDto(rating))
    }
}
