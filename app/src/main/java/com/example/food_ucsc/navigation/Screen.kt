package com.example.food_ucsc.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Category : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }
    object Profile : Screen("profile")
    object NutritionalInfo : Screen("nutritional_info")
    object OrderHistory : Screen("order_history")
    object Rating : Screen("rating/{orderId}") {
        fun createRoute(orderId: String) = "rating/$orderId"
    }
}
