package com.example.food_ucsc

import android.app.Application
import com.example.food_ucsc.data.remote.RetrofitClient
import com.example.food_ucsc.data.repository.FoodRepository

class FoodUcscApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer()
    }
}

interface AppContainer {
    val foodRepository: FoodRepository
}

class AppDataContainer : AppContainer {
    override val foodRepository: FoodRepository by lazy {
        FoodRepository(RetrofitClient.apiService)
    }
}
