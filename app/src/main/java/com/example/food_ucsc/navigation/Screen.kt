package com.example.food_ucsc.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Register : Screen("register")
    object Explore : Screen("explore")
    object Map : Screen("map")
    object AllCategories : Screen("all_categories")
    object Category : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }
    object RestaurantDetail : Screen("restaurant/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "restaurant/$restaurantId"
    }
    object Profile : Screen("profile")
    object NutritionalInfo : Screen("nutritional_info")
    object OrderHistory : Screen("order_history")
    object Rating : Screen("rating/{orderId}") {
        fun createRoute(orderId: String) = "rating/$orderId"
    }
}
