package com.example.food_ucsc.ui.state

data class ProfileUiState(
    val userName: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val isLoading: Boolean = false
)
