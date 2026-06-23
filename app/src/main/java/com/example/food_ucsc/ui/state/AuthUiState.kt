package com.example.food_ucsc.ui.state

import com.example.food_ucsc.ui.models.AppUser

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val user: AppUser? = null
)
