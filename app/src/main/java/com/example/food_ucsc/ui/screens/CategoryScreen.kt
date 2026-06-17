package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.ui.models.Category
import com.example.food_ucsc.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryName: String, 
    navController: NavController,
    viewModel: CategoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryName) {
        viewModel.loadCategoryItems(categoryName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
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
        } else {
            if (uiState.categories.isNotEmpty()) {
                // Si hay categorías (caso "Otros"), mostramos la cuadrícula
                CategoryGrid(
                    categories = uiState.categories,
                    innerPadding = innerPadding,
                    onCategoryClick = { selectedCategory ->
                        // Navegamos a la misma pantalla con la nueva categoría
                        navController.navigate("category/${selectedCategory.name}") {
                            // Opcional: limpiar el stack para no acumular pantallas si lo prefieres
                        }
                    }
                )
            } else {
                // Si no, mostramos la lista de platos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items) { item ->
                        FoodItemCard(item = item)
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
fun FoodItemCard(item: com.example.food_ucsc.ui.models.FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "$${item.price}", style = MaterialTheme.typography.labelLarge, color = Color(0xFF6750A4))
            }
        }
    }
}
