package com.example.food_ucsc.data.repository

import com.example.food_ucsc.data.remote.service.ApiService
import com.example.food_ucsc.data.remote.toDomain
import com.example.food_ucsc.data.remote.toDomainOrNull
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

    suspend fun getTips(): List<HealthTip> = withContext(Dispatchers.IO) {
        apiService.getTips().map { it.toDomain() }
    }

    suspend fun getChallenges(): List<Challenge> = withContext(Dispatchers.IO) {
        apiService.getChallenges().map { it.toDomain() }
    }
}
