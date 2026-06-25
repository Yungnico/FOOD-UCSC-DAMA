package com.example.food_ucsc

import android.app.Application
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.di.NetworkModule
import com.example.food_ucsc.di.RepositoryModule

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
    override val sessionManager: SessionManager by lazy {
        RepositoryModule.provideSessionManager(application)
    }

    override val foodRepository: FoodRepository by lazy {
        RepositoryModule.provideFoodRepository(
            NetworkModule.provideApiService(
                NetworkModule.provideRetrofit(
                    NetworkModule.provideOkHttpClient(
                        NetworkModule.provideAuthInterceptor(sessionManager)
                    )
                )
            )
        )
    }
}
