package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.models.Order
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.OrderHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF8FF))
            )
        },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "No se pudo cargar el historial",
                        color = Color(0xFFB3261E),
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.orders.isEmpty()) {
                        item {
                            Text(
                                text = "Todavía no tienes compras registradas.",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(uiState.orders) { order ->
                            OrderItemCard(
                                order = order,
                                onRateClick = {
                                    navController.navigate(Screen.Rating.createRoute(order.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order, onRateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEADDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFF21005D))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = order.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = order.date, color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Text(
                    text = "$${order.total}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = order.items.joinToString(", "),
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (order.isRated) {
                    Text(
                        text = "Calificado",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    TextButton(onClick = onRateClick) {
                        Text("Calificar")
                    }
                }
            }
        }
    }
}
