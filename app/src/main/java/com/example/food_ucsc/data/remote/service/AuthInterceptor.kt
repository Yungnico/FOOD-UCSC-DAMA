package com.example.food_ucsc.data.remote.service

import com.example.food_ucsc.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val existingAuth = original.header("Authorization")
        val token = sessionManager.getToken()

        val request = if (existingAuth.isNullOrBlank() && !token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}