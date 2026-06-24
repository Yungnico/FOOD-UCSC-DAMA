package com.example.food_ucsc.data.repository

import com.example.food_ucsc.data.remote.dto.LoginRequestDto
import com.example.food_ucsc.data.remote.dto.NutritionSummaryDto
import com.example.food_ucsc.data.remote.dto.ProductDetailDto
import com.example.food_ucsc.data.remote.dto.PurchaseRegistrationRequestDto
import com.example.food_ucsc.data.remote.dto.PurchaseRatingUpdateDto
import com.example.food_ucsc.data.remote.dto.PurchaseDto
import com.example.food_ucsc.data.remote.dto.RegisterRequestDto
import com.example.food_ucsc.data.remote.toDomain as toCategoryDomain
import com.example.food_ucsc.data.remote.service.ApiService
import com.example.food_ucsc.data.remote.toDomain
import com.example.food_ucsc.data.remote.toOrder
import com.example.food_ucsc.data.remote.toFoodItem
import com.example.food_ucsc.data.remote.toDomainOrNull
import com.example.food_ucsc.ui.models.AppUser
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.models.Menu
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.Order
import com.example.food_ucsc.ui.models.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodRepository(private val apiService: ApiService) {

    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        apiService.getRestaurants().map { it.toDomain() }
    }

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        apiService.getCategories().map { it.toCategoryDomain() }
    }

    suspend fun getProducts(): List<FoodItem> = withContext(Dispatchers.IO) {
        apiService.getProducts().map { it.toFoodItem() }
    }

    suspend fun getProductDetails(): List<ProductDetailDto> = withContext(Dispatchers.IO) {
        apiService.getProducts()
    }

    suspend fun getRestaurantById(id: Int): Restaurant = withContext(Dispatchers.IO) {
        apiService.getRestaurantById(id).toDomain()
    }

    suspend fun getMenusByRestaurant(restaurantId: Int): List<Menu> = withContext(Dispatchers.IO) {
        apiService.getMenusByRestaurant(restaurantId).map { it.toDomain() }
    }

    suspend fun getFavoritesByUser(userId: Int) = withContext(Dispatchers.IO) {
        apiService.getFavoritesByUser(userId)
            .mapNotNull { it.toDomainOrNull() }
    }

    suspend fun getMyFavorites(token: String) = withContext(Dispatchers.IO) {
        apiService.getMyFavorites("Bearer $token")
            .mapNotNull { it.toDomainOrNull() }
    }

    suspend fun login(email: String, password: String): Pair<String, AppUser> = withContext(Dispatchers.IO) {
        val response = apiService.login(LoginRequestDto(email = email, password = password))
        response.token to response.user.toDomain()
    }

    suspend fun register(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        email: String,
        password: String
    ): Pair<String, AppUser> = withContext(Dispatchers.IO) {
        val response = apiService.register(
            RegisterRequestDto(
                nombre = nombre,
                apellidoPaterno = apellidoPaterno.ifBlank { null },
                apellidoMaterno = apellidoMaterno.ifBlank { null },
                email = email,
                password = password
            )
        )
        response.token to response.user.toDomain()
    }

    suspend fun me(token: String): AppUser = withContext(Dispatchers.IO) {
        apiService.me("Bearer $token").toDomain()
    }

    suspend fun getNutritionSummary(token: String): NutritionSummaryDto = withContext(Dispatchers.IO) {
        apiService.getNutritionSummary("Bearer $token")
    }

    suspend fun logout(token: String) = withContext(Dispatchers.IO) {
        apiService.logout("Bearer $token")
    }

    suspend fun getMyPurchases(token: String): List<Order> = withContext(Dispatchers.IO) {
        apiService.getMyPurchases("Bearer $token").map { it.toOrder() }
    }

    suspend fun getMyPurchaseDetails(token: String): List<PurchaseDto> = withContext(Dispatchers.IO) {
        apiService.getMyPurchases("Bearer $token")
    }

    suspend fun ratePurchase(token: String, purchaseId: Int, rating: Int): Order = withContext(Dispatchers.IO) {
        apiService.updatePurchaseRating(
            authorization = "Bearer $token",
            purchaseId = purchaseId,
            request = PurchaseRatingUpdateDto(calificacion = rating)
        ).toOrder()
    }

    suspend fun getTips(): List<HealthTip> = withContext(Dispatchers.IO) {
        apiService.getTips().map { it.toDomain() }
    }

    suspend fun getChallenges(): List<Challenge> = withContext(Dispatchers.IO) {
        apiService.getChallenges().map { it.toDomain() }
    }
}
