package com.example.food_ucsc.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.food_ucsc.FoodUcscApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                foodUcscApplication().container.foodRepository,
                foodUcscApplication().container.sessionManager
            )
        }
        initializer {
            RestaurantViewModel(
                foodUcscApplication().container.foodRepository
            )
        }
        initializer {
            CategoryViewModel(
                foodUcscApplication().container.foodRepository
            )
        }
        initializer {
            ExploreViewModel(
                foodUcscApplication().container.foodRepository
            )
        }
        initializer {
            OrderHistoryViewModel(
                foodUcscApplication().container.foodRepository,
                foodUcscApplication().container.sessionManager
            )
        }
        initializer {
            NutritionalInfoViewModel(
                foodUcscApplication().container.foodRepository,
                foodUcscApplication().container.sessionManager
            )
        }
        initializer {
            RatingViewModel(
                foodUcscApplication().container.foodRepository,
                foodUcscApplication().container.sessionManager
            )
        }
        initializer {
            AuthViewModel(
                foodUcscApplication().container.foodRepository,
                foodUcscApplication().container.sessionManager
            )
        }
    }
}

fun CreationExtras.foodUcscApplication(): FoodUcscApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FoodUcscApplication)
