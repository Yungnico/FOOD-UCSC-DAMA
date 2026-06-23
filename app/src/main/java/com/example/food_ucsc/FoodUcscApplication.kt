package com.example.food_ucsc

import android.app.Application
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.remote.RetrofitClient
import com.example.food_ucsc.data.repository.FoodRepository

class FoodUcscApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

interface AppContainer {
    val foodRepository: FoodRepository
    val sessionManager: SessionManager
}

class AppDataContainer(private val application: Application) : AppContainer {
    override val foodRepository: FoodRepository by lazy {
        FoodRepository(RetrofitClient.apiService)
    }

    override val sessionManager: SessionManager by lazy {
        SessionManager(application.applicationContext)
    }
}
