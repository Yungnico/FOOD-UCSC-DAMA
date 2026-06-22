package com.example.food_ucsc.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_ucsc.data.repository.FoodRepository
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val defaultUserId = 1

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val categories = mockCategories()
            val recommended = mockRecommendedItems()
            val favorites = runCatching {
                foodRepository.getFavoritesByUser(defaultUserId)
            }.getOrElse { emptyList() }
            val tips = runCatching {
                foodRepository.getTips()
            }.getOrElse { emptyList() }
            val challenges = runCatching {
                foodRepository.getChallenges()
            }.getOrElse { emptyList() }

            _uiState.update {
                it.copy(
                    categories = categories,
                    recommendedItems = recommended,
                    favoriteItems = if (favorites.isEmpty()) mockFavorites() else favorites,
                    tips = if (tips.isEmpty()) mockTips() else tips,
                    challenges = if (challenges.isEmpty()) mockChallenges() else challenges,
                    isLoading = false
                )
            }
        }
    }

    private fun mockCategories(): List<Category> {
        return listOf(
            Category("Comida Rápida", Icons.Default.Fastfood),
            Category("Saludable", Icons.Default.Restaurant),
            Category("Vegetariana", Icons.Default.Grass),
            Category("Vegana", Icons.Default.Eco),
            Category("Postres", Icons.Default.Cake),
            Category("Bebidas", Icons.Default.LocalDrink),
            Category("Snacks", Icons.Default.LunchDining),
            Category("Otros", Icons.Default.MoreHoriz)
        )
    }

    private fun mockRecommendedItems(): List<FoodItem> {
        return listOf(
            FoodItem(1, "Sándwich Ave", "Pollo con mayonesa", 2500.0, "Snacks", 10, Icons.Default.LunchDining),
            FoodItem(2, "Ensalada César", "Lechuga, crutones, queso", 3000.0, "Saludable", 5, Icons.Default.Restaurant),
            FoodItem(3, "Jugo Frambuesa", "Fruta natural 500ml", 1500.0, "Bebidas", 20, Icons.Default.LocalDrink)
        )
    }

    private fun mockFavorites(): List<FoodItem> {
        return listOf(
            FoodItem(4, "Papas Fritas", "Porción familiar", 2000.0, "Comida Rápida", 15, Icons.Default.Fastfood),
            FoodItem(5, "Muffin Chocolate", "Recién horneado", 1200.0, "Postres", 8, Icons.Default.Cake),
            FoodItem(6, "Pizza Slice", "Peperoni y queso", 1800.0, "Comida Rápida", 12, Icons.Default.LocalPizza)
        )
    }

    private fun mockTips(): List<HealthTip> {
        return listOf(
            HealthTip(1, "Prefiere agua en lugar de bebidas azucaradas durante la jornada.", "Hidratación"),
            HealthTip(2, "Incluye una porción de verduras en tu almuerzo para mejorar saciedad.", "Balance"),
            HealthTip(3, "Evita frituras en días consecutivos para reducir grasas saturadas.", "Prevención")
        )
    }

    private fun mockChallenges(): List<Challenge> {
        return listOf(
            Challenge(1, "Semana sin frituras", "Elige preparaciones al horno o plancha por 5 días.", 120),
            Challenge(2, "Más verduras", "Agrega verduras a tus comidas principales durante la semana.", 90)
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
