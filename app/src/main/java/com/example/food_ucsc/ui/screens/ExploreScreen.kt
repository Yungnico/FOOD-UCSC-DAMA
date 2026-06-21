package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.components.BottomNavBar
import com.example.food_ucsc.ui.components.RestaurantCard
import com.example.food_ucsc.ui.models.Restaurant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Datos de ejemplo para los locales actualizados con los nuevos atributos
    val restaurants = listOf(
        Restaurant(
            id = 1,
            nombre = "Casino Central",
            descripcion = "Almuerzos y colaciones",
            horario = "08:00 - 18:00",
            contacto = "+56 41 273 5000",
            latitude = -36.801,
            longitude = -73.013,
            tiempo_espera_estatico = "15-20 min",
            rating = 4.5,
            icon = Icons.Default.Restaurant,
            bannerColor = 0xFF6750A4
        ),
        Restaurant(
            id = 2,
            nombre = "Cafetería Biblioteca",
            descripcion = "Café, snacks y sándwiches",
            horario = "09:00 - 20:00",
            contacto = "cafeteria@ucsc.cl",
            latitude = -36.802,
            longitude = -73.014,
            tiempo_espera_estatico = "5-10 min",
            rating = 4.8,
            icon = Icons.Default.Coffee,
            bannerColor = 0xFF3F51B5
        ),
        Restaurant(
            id = 3,
            nombre = "Kiosko Saludable",
            descripcion = "Ensaladas y jugos naturales",
            horario = "09:00 - 17:00",
            contacto = "N/A",
            latitude = -36.803,
            longitude = -73.015,
            tiempo_espera_estatico = "10-15 min",
            rating = 4.2,
            icon = Icons.Default.Grass,
            bannerColor = 0xFF4CAF50
        ),
        Restaurant(
            id = 4,
            nombre = "Pizzería Express",
            descripcion = "Pizzas y comida rápida",
            horario = "11:00 - 16:00",
            contacto = "pizzeria@ucsc.cl",
            latitude = -36.804,
            longitude = -73.016,
            tiempo_espera_estatico = "20-30 min",
            rating = 4.0,
            icon = Icons.Default.LocalPizza,
            bannerColor = 0xFFFF9800
        ),
        Restaurant(
            id = 5,
            nombre = "Dulce Tentación",
            descripcion = "Pasteles y repostería",
            horario = "10:00 - 19:00",
            contacto = "+56 9 1234 5678",
            latitude = -36.805,
            longitude = -73.017,
            tiempo_espera_estatico = "5 min",
            rating = 4.7,
            icon = Icons.Default.Cake,
            bannerColor = 0xFFE91E63
        )
    )

    val filters = listOf("Todos", "Cerca", "Populares", "Baratos", "Saludables")
    var selectedFilter by remember { mutableStateOf("Todos") }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Buscador (Estético)
            PaddingValues(16.dp).let { padding ->
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text(stringResource(R.string.search)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F0F8),
                        unfocusedContainerColor = Color(0xFFF3F0F8),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Filtros
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEADDFF),
                            selectedLabelColor = Color(0xFF21005D)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Locales
            Text(
                text = "Locales disponibles",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(restaurants) { restaurant ->
                    RestaurantCard(restaurant = restaurant, onClick = {
                        navController.navigate(Screen.RestaurantDetail.createRoute(restaurant.id))
                    })
                }
            }
        }
    }
}
