package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.components.BottomNavBar
import com.example.food_ucsc.ui.components.FoodItemCard
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    if (uiState.showRatingDialog && uiState.pendingOrderId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRatingDialog() },
            title = { Text("¡Tu opinión nos importa!") },
            text = { Text("¿Cómo estuvo tu último pedido? Ayúdanos a mejorar calificando tu experiencia.") },
            confirmButton = {
                Button(
                    onClick = {
                        val orderId = uiState.pendingOrderId!!
                        viewModel.dismissRatingDialog()
                        navController.navigate(Screen.Rating.createRoute(orderId))
                    }
                ) {
                    Text("Calificar Ahora")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRatingDialog() }) {
                    Text("Más tarde")
                }
            }
        )
    }

    if (uiState.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleFilterSheet(false) },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            NutritionalFilterContent(
                selectedFilter = uiState.selectedNutritionalFilter,
                onFilterSelected = { viewModel.onFilterSelected(it) }
            )
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Header(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onFilterClick = { viewModel.toggleFilterSheet(true) },
                hasActiveFilter = uiState.selectedNutritionalFilter != "Todos"
            )

            // Caso: Cargando datos iniciales
            if (uiState.isLoading && uiState.categories.isEmpty() && uiState.recommendedItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6750A4))
                }
            } 
            // Caso: Hay búsqueda o filtros activos
            else if (uiState.searchQuery.isNotEmpty() || uiState.selectedNutritionalFilter != "Todos") {
                Text(
                    text = if (uiState.searchQuery.isNotEmpty()) "Resultados para \"${uiState.searchQuery}\"" else "Filtrado por: ${uiState.selectedNutritionalFilter}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
                
                if (uiState.searchResults.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron productos", color = Color.Gray)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.searchResults.forEach { item ->
                            FoodItemCard(
                                item = item,
                                isFavorite = uiState.favoriteProductIds.contains(item.id),
                                onFavoriteClick = { viewModel.toggleFavorite(item.id) },
                                onClick = { /* Detalle del producto */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } 
            // Caso: Home normal
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    CategorySection(categories = uiState.categories, navController = navController)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    RecommendedSection(
                        recommendedItems = uiState.recommendedItems,
                        favoriteIds = uiState.favoriteProductIds,
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    FavouriteSection(
                        favoriteItems = uiState.favoriteItems,
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    TipsSection(tips = uiState.tips)
                    Spacer(modifier = Modifier.height(24.dp))
                    ChallengesSection(challenges = uiState.challenges)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(query: String, onQueryChange: (String) -> Unit, onFilterClick: () -> Unit, hasActiveFilter: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 0.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(width = 80.dp, height = 64.dp).clip(RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)).background(Color(0xFFF59E0B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f).height(56.dp),
            placeholder = { Text(stringResource(R.string.search), color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            trailingIcon = {
                Row {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = Color.Gray)
                        }
                    }
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.Tune, 
                            contentDescription = "Filtros",
                            tint = if (hasActiveFilter) Color(0xFF6750A4) else Color.Gray
                        )
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F0F8),
                unfocusedContainerColor = Color(0xFFF3F0F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}

@Composable
fun RecommendedSection(
    recommendedItems: List<FoodItem>,
    favoriteIds: Set<Int>,
    onFavoriteToggle: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = stringResource(R.string.recommended), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            recommendedItems.forEach { item ->
                ProductCard(
                    item = item,
                    isFavorite = favoriteIds.contains(item.id),
                    onFavoriteClick = { onFavoriteToggle(item.id) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(item: FoodItem, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier.width(150.dp).border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.nombre, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$${item.precio_base.toInt()}", fontSize = 14.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.ExtraBold)
            }
            IconButton(onClick = onFavoriteClick, modifier = Modifier.align(Alignment.TopEnd).size(36.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color(0xFFF59E0B) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FavouriteSection(favoriteItems: List<FoodItem>, onFavoriteToggle: (Int) -> Unit) {
    if (favoriteItems.isEmpty()) return
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = stringResource(R.string.favorite), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            favoriteItems.forEach { item ->
                Box(modifier = Modifier.size(100.dp)) {
                    Card(modifier = Modifier.fillMaxSize().border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        }
                    }
                    IconButton(onClick = { onFavoriteToggle(item.id) }, modifier = Modifier.align(Alignment.TopEnd).size(32.dp)) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionalFilterContent(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("Todos", "Bajo en calorías", "Proteico", "Saludable")
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
        Text("Filtros Nutricionales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        filters.forEach { filter ->
            Row(modifier = Modifier.fillMaxWidth().clickable { onFilterSelected(filter) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(filter, color = if (selectedFilter == filter) Color(0xFF6750A4) else Color.Black, fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal)
                if (selectedFilter == filter) Icon(Icons.Default.Check, null, tint = Color(0xFF6750A4))
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun CategorySection(categories: List<Category>, navController: NavController) {
    val displayCategories = categories.filter { it.name != "Otros" }.take(5) +
            (categories.find { it.name == "Otros" } ?: Category("Otros", Icons.Default.MoreHoriz))
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = stringResource(R.string.category), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            displayCategories.chunked(3).forEachIndexed { index, rowCategories ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowCategories.forEach { category ->
                        CategoryItem(
                            name = category.name, icon = category.icon, modifier = Modifier.weight(1f),
                            onClick = {
                                if (category.name == "Otros") navController.navigate(Screen.AllCategories.route)
                                else navController.navigate(Screen.Category.createRoute(category.name))
                            }
                        )
                    }
                    if (rowCategories.size < 3) repeat(3 - rowCategories.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
                if (index < displayCategories.chunked(3).size - 1) Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.clickable { onClick() }) {
        Surface(modifier = Modifier.size(72.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFFEADDFF), shadowElevation = 2.dp) {
            Icon(imageVector = icon, contentDescription = name, modifier = Modifier.padding(20.dp), tint = Color(0xFF21005D))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, fontSize = 14.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium, maxLines = 2)
    }
}

@Composable
fun TipsSection(tips: List<HealthTip>) {
    if (tips.isEmpty()) return
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Consejos rápidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        tips.take(3).forEach { tip ->
            Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = tip.categoria, style = MaterialTheme.typography.labelMedium, color = Color(0xFF6750A4), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = tip.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ChallengesSection(challenges: List<Challenge>) {
    if (challenges.isEmpty()) return
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Desafíos saludables", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        challenges.take(3).forEach { challenge ->
            Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFF5F1FF)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = challenge.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = challenge.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Recompensa: ${challenge.recompensaPuntos} pts", style = MaterialTheme.typography.labelMedium, color = Color(0xFF4A2B8A))
                }
            }
        }
    }
}
