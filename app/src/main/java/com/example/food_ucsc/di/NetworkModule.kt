package com.example.food_ucsc.di

import com.example.food_ucsc.data.local.SessionManager
import com.example.food_ucsc.data.remote.service.AuthInterceptor
import com.example.food_ucsc.data.remote.service.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    // Reemplazar -> http://10.0.2.2:8000/api/ por http://IPV4_DE_TU_PC:8000/api/
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}