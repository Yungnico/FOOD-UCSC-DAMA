package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val displayName = authUiState.user?.nombre?.takeIf { it.isNotBlank() } ?: "Usuario UCSC"
    val displayEmail = authUiState.user?.email ?: "usuario@ucsc.cl"
    val userPoints = authUiState.user?.puntos ?: 0
    val calorieTarget = authUiState.user?.caloriasTarget ?: 2200
    val objectives = authUiState.user?.objetivosSalud ?: "Sin objetivo definido"
    val calorieProgress = (userPoints.coerceAtMost(calorieTarget)).toFloat() / calorieTarget.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFBF8FF)
                )
            )
        },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Info Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDE68A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFF92400E)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = displayEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF3F0F8)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Puntos acumulados",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF6750A4)
                    )
                    Text(
                        text = "$userPoints pts",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { calorieProgress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF6750A4),
                        trackColor = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Objetivo de salud: $objectives",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Meta calórica diaria: $calorieTarget kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Menu Options
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "Información nutricional",
                subtitle = "Dashboard, calorías y estadísticas",
                onClick = { navController.navigate(Screen.NutritionalInfo.route) }
            )
            
            ProfileMenuItem(
                icon = Icons.Default.History,
                title = "Historial de compras",
                subtitle = "Mis pedidos anteriores y calificar",
                onClick = { navController.navigate(Screen.OrderHistory.route) }
            )
            
            ProfileMenuItem(
                icon = Icons.Default.RestaurantMenu,
                title = "Preferencias alimentarias",
                subtitle = "Alergias, gustos y alertas de comida",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F0F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF6750A4))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
