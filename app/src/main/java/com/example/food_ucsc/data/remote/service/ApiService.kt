package com.example.food_ucsc.data.remote.service

import com.example.food_ucsc.data.remote.dto.ChallengeDto
import com.example.food_ucsc.data.remote.dto.FavoriteDto
import com.example.food_ucsc.data.remote.dto.MenuDto
import com.example.food_ucsc.data.remote.dto.RestaurantDto
import com.example.food_ucsc.data.remote.dto.TipDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("locales")
    suspend fun getRestaurants(): List<RestaurantDto>

    @GET("locales/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): RestaurantDto

    @GET("locales/{id}/menus")
    suspend fun getMenusByRestaurant(@Path("id") restaurantId: Int): List<MenuDto>

    @GET("usuarios/{id}/favoritos")
    suspend fun getFavoritesByUser(@Path("id") userId: Int): List<FavoriteDto>

    @GET("consejos")
    suspend fun getTips(): List<TipDto>

    @GET("desafios")
    suspend fun getChallenges(): List<ChallengeDto>
}
