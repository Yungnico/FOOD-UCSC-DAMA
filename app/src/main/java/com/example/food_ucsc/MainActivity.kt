package com.example.food_ucsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.screens.*
import com.example.food_ucsc.ui.theme.FoodUCSC_Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodUCSC_Theme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController = navController)
                    }
                    composable(Screen.Category.route) { backStackEntry ->
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                        CategoryScreen(categoryName = categoryName, navController = navController)
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(navController = navController)
                    }
                    composable(Screen.NutritionalInfo.route) {
                        NutritionalInfoScreen(navController = navController)
                    }
                    composable(Screen.OrderHistory.route) {
                        OrderHistoryScreen(navController = navController)
                    }
                    composable(Screen.Rating.route) { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: "0"
                        RatingScreen(navController = navController, orderId = orderId)
                    }
                }
            }
        }
    }
}
