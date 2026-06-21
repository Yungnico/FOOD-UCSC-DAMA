package com.example.food_ucsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.screens.AllCategoriesScreen
import com.example.food_ucsc.ui.screens.CategoryScreen
import com.example.food_ucsc.ui.screens.ExploreScreen
import com.example.food_ucsc.ui.screens.HomeScreen
import com.example.food_ucsc.ui.screens.NutritionalInfoScreen
import com.example.food_ucsc.ui.screens.ProfileScreen
import com.example.food_ucsc.ui.screens.RestaurantDetailScreen
import com.example.food_ucsc.ui.theme.FoodUCSC_Theme
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.HomeViewModel

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
                    composable(Screen.AllCategories.route) {
                        val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by viewModel.uiState.collectAsState()
                        AllCategoriesScreen(
                            navController = navController,
                            categories = uiState.categories
                        )
                    }
                    composable(Screen.Explore.route) {
                        ExploreScreen(navController = navController)
                    }
                    composable(Screen.RestaurantDetail.route) { backStackEntry ->
                        val restaurantId = backStackEntry.arguments?.getString("restaurantId")?.toIntOrNull() ?: 0
                        RestaurantDetailScreen(restaurantId = restaurantId, navController = navController)
                    }
                }
            }
        }
    }
}
