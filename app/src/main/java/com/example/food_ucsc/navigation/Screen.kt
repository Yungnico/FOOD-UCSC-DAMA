package com.example.food_ucsc.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Category : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }
    object AllCategories : Screen("all_categories")
    object Explore : Screen("explore")
    object Profile : Screen("profile")
    object NutritionalInfo : Screen("nutritional_info")
    object RestaurantDetail : Screen("restaurant/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "restaurant/$restaurantId"
    }
}
