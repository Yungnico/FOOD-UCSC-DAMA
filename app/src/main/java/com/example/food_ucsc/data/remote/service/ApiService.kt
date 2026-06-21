package com.example.food_ucsc.data.remote.service

import com.example.food_ucsc.data.remote.dto.MenuDto
import com.example.food_ucsc.data.remote.dto.RestaurantDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("locales")
    suspend fun getRestaurants(): List<RestaurantDto>

    @GET("locales/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): RestaurantDto

    @GET("locales/{id}/menus")
    suspend fun getMenusByRestaurant(@Path("id") restaurantId: Int): List<MenuDto>
}
