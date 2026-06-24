package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.ui.components.FoodItemCard
import com.example.food_ucsc.ui.models.Restaurant
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurantId: Int,
    navController: NavController,
    viewModel: RestaurantViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(restaurantId) {
        viewModel.loadRestaurantDetails(restaurantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.restaurant?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6750A4),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.error ?: "No se pudo cargar el detalle del local",
                    color = Color(0xFFB3261E),
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else if (uiState.restaurant != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Banner / Header Info
                item {
                    RestaurantHeader(uiState.restaurant!!)
                }

                // Menus and Products
                uiState.menus.forEach { menu ->
                    item {
                        MenuHeader(menu.titulo, menu.promociones)
                    }
                    items(menu.productos) { product ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            FoodItemCard(item = product)
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun RestaurantHeader(restaurant: Restaurant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(restaurant.bannerColor))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.2f)
        ) {
            Icon(
                imageVector = restaurant.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = restaurant.nombre,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = restaurant.descripcion,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoChip(icon = Icons.Default.AccessTime, text = "${restaurant.tiempo_espera_estimado} min")
            InfoChip(icon = Icons.Default.Star, text = restaurant.rating.toString())
            InfoChip(icon = Icons.Default.Phone, text = "Contacto")
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MenuHeader(titulo: String, promociones: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D1B20)
        )
        if (promociones.isNotEmpty()) {
            Surface(
                color = Color(0xFFFFFBFE),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6750A4).copy(alpha = 0.3f)),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = Color(0xFF6750A4),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = promociones,
                        color = Color(0xFF6750A4),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
