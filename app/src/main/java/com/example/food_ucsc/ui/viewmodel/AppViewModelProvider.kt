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
                foodUcscApplication().container.foodRepository
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
    }
}

fun CreationExtras.foodUcscApplication(): FoodUcscApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FoodUcscApplication)
