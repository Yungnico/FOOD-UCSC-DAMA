package com.example.food_ucsc.data.repository

import com.example.food_ucsc.data.remote.dto.LoginRequestDto
import com.example.food_ucsc.data.remote.dto.RegisterRequestDto
import com.example.food_ucsc.data.remote.service.ApiService
import com.example.food_ucsc.data.remote.toDomain
import com.example.food_ucsc.data.remote.toDomainOrNull
import com.example.food_ucsc.ui.models.AppUser
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.models.Menu
import com.example.food_ucsc.ui.models.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodRepository(private val apiService: ApiService) {

    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        apiService.getRestaurants().map { it.toDomain() }
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

    suspend fun logout(token: String) = withContext(Dispatchers.IO) {
        apiService.logout("Bearer $token")
    }

    suspend fun getTips(): List<HealthTip> = withContext(Dispatchers.IO) {
        apiService.getTips().map { it.toDomain() }
    }

    suspend fun getChallenges(): List<Challenge> = withContext(Dispatchers.IO) {
        apiService.getChallenges().map { it.toDomain() }
    }
}
