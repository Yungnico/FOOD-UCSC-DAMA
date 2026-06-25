package com.example.food_ucsc.di

import android.app.Application
import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.remote.service.ApiService
import com.example.food_ucsc.data.repository.FoodRepository

object RepositoryModule {

    fun provideFoodRepository(apiService: ApiService): FoodRepository {
        return FoodRepository(apiService)
    }

    fun provideSessionManager(application: Application): SessionManager {
        return SessionManager(application.applicationContext)
    }
}