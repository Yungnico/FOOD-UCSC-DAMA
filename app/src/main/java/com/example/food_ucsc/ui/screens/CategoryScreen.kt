package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.components.FoodItemCard
import com.example.food_ucsc.ui.components.RestaurantCard
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryName: String, 
    navController: NavController,
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryName) {
        viewModel.loadCategoryItems(categoryName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            if (uiState.categories.isNotEmpty()) {
                CategoryGrid(
                    categories = uiState.categories,
                    innerPadding = innerPadding,
                    onCategoryClick = { selectedCategory ->
                        navController.navigate(Screen.Category.createRoute(selectedCategory.name))
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.restaurants.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.locals_in_category),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        items(uiState.restaurants) { restaurant ->
                            RestaurantCard(restaurant = restaurant, onClick = {
                                navController.navigate(Screen.RestaurantDetail.createRoute(restaurant.id))
                            })
                        }
                    }
                    
                    if (uiState.items.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.popular_dishes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        items(uiState.items) { item ->
                            FoodItemCard(
                                item = item,
                                isFavorite = uiState.favoriteProductIds.contains(item.id),
                                onFavoriteClick = { viewModel.toggleFavorite(item.id) },
                                onClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<Category>, 
    innerPadding: PaddingValues,
    onCategoryClick: (Category) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        categories.chunked(3).forEachIndexed { index, rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCategories.forEach { category ->
                    CategoryItemSimple(
                        name = category.name,
                        icon = category.icon,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
            if (index < categories.chunked(3).size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryItemSimple(
    name: String, 
    icon: ImageVector, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.padding(20.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
