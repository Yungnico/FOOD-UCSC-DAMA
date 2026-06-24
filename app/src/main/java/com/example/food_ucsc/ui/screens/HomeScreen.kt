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
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.models.Challenge
import com.example.food_ucsc.ui.models.FoodItem
import com.example.food_ucsc.ui.models.HealthTip
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

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

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Header()
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(
                categories = uiState.categories,
                navController = navController
            )
            Spacer(modifier = Modifier.height(24.dp))
            RecommendedSection(recommendedItems = uiState.recommendedItems)
            Spacer(modifier = Modifier.height(24.dp))
            FavouriteSection(favoriteItems = uiState.favoriteItems)
            Spacer(modifier = Modifier.height(24.dp))
            TipsSection(tips = uiState.tips)
            Spacer(modifier = Modifier.height(24.dp))
            ChallengesSection(challenges = uiState.challenges)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 0.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 64.dp)
                .clip(RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                .background(Color(0xFFF59E0B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = "Menú de comida",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFF3F0F8)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_here),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = stringResource(R.string.menu),
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun CategorySection(categories: List<Category>, navController: NavController) {
    val displayCategories = categories.filter { it.name != "Otros" }.take(5) +
            (categories.find { it.name == "Otros" } ?: Category("Otros", Icons.Default.MoreHoriz))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.category),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column {
            displayCategories.chunked(3).forEachIndexed { index, rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCategories.forEach { category ->
                        CategoryItem(
                            name = category.name,
                            icon = category.icon,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (category.name == "Otros") {
                                    navController.navigate(Screen.AllCategories.route)
                                } else {
                                    navController.navigate(Screen.Category.createRoute(category.name))
                                }
                            }
                        )
                    }
                    // Si la fila no está completa (menos de 3), rellenamos con Espaciadores con peso
                    if (rowCategories.size < 3) {
                        repeat(3 - rowCategories.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (index < displayCategories.chunked(3).size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
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
            color = Color(0xFFEADDFF),
            shadowElevation = 2.dp
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.padding(20.dp),
                tint = Color(0xFF21005D)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp,
            maxLines = 2
        )
    }
}

@Composable
fun RecommendedSection(recommendedItems: List<FoodItem>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recommended),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            recommendedItems.forEach { item ->
                ProductCard(item.nombre, item.icon)
            }
        }
    }
}

@Composable
fun ProductCard(name: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = name, modifier = Modifier.size(40.dp), tint = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Text(text = name, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun FavouriteSection(favoriteItems: List<FoodItem>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.favorite),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            favoriteItems.forEach { item ->
                FavouriteCard(item.icon)
            }
        }
    }
}

@Composable
fun FavouriteCard(icon: ImageVector) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
        }
    }
}

@Composable
fun TipsSection(tips: List<HealthTip>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Consejos rápidos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        tips.take(3).forEach { tip ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = tip.categoria,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6750A4),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = tip.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ChallengesSection(challenges: List<Challenge>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Desafíos saludables",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        challenges.take(3).forEach { challenge ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F1FF)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = challenge.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = challenge.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Recompensa: ${challenge.recompensaPuntos} pts",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4A2B8A)
                    )
                }
            }
        }
    }
}
