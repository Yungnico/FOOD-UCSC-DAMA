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
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showIntervalDialog by remember { mutableStateOf(false) }
    val allFilter = stringResource(R.string.all)

    // Comprobar pedidos pendientes al entrar
    LaunchedEffect(Unit) {
        viewModel.checkPendingRatings()
    }

    // Diálogo de calificación de pedido
    if (uiState.showRatingDialog && uiState.pendingOrderId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRatingDialog() },
            title = { Text(stringResource(R.string.thank_you)) },
            text = { Text(stringResource(R.string.rate_last_order_prompt)) },
            confirmButton = {
                Button(
                    onClick = {
                        val orderId = uiState.pendingOrderId!!
                        viewModel.dismissRatingDialog()
                        navController.navigate(Screen.Rating.createRoute(orderId))
                    }
                ) {
                    Text(stringResource(R.string.rate_now))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRatingDialog() }) {
                    Text(stringResource(R.string.later))
                }
            }
        )
    }

    // Diálogo de Recordatorio de Agua
    if (uiState.showWaterReminder) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissWaterReminder() },
            icon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(48.dp)) },
            title = { Text(stringResource(R.string.water_reminder_title), fontWeight = FontWeight.Bold) },
            text = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.waterReminderPhrase, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showIntervalDialog = true }) {
                        Text(stringResource(R.string.water_reminder_change_frequency, uiState.waterReminderIntervalMinutes))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissWaterReminder() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }

    // Diálogo para elegir el tiempo del recordatorio
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            title = { Text(stringResource(R.string.water_reminder_frequency_title)) },
            text = {
                Column {
                    val intervals = listOf(30, 60, 90, 120)
                    intervals.forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    viewModel.setWaterReminderInterval(minutes)
                                    showIntervalDialog = false 
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.waterReminderIntervalMinutes == minutes,
                                onClick = { 
                                    viewModel.setWaterReminderInterval(minutes)
                                    showIntervalDialog = false 
                                }
                            )
                            Text(text = stringResource(R.string.water_reminder_every_minutes, minutes), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIntervalDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Modal para los filtros nutricionales
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
                hasActiveFilter = uiState.selectedNutritionalFilter != allFilter
            )

            if (uiState.searchQuery.isNotEmpty() || uiState.selectedNutritionalFilter != allFilter) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotEmpty()) 
                            "Resultados para \"${uiState.searchQuery}\"" 
                            else "Filtrado por: ${uiState.selectedNutritionalFilter}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState.selectedNutritionalFilter != allFilter) {
                        AssistChip(
                            onClick = { viewModel.onFilterSelected(allFilter) },
                            label = { Text(uiState.selectedNutritionalFilter) },
                            trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) },
                            colors = AssistChipDefaults.assistChipColors(labelColor = Color(0xFF6750A4))
                        )
                    }
                }
                
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
                                onClick = { /* Detalle del producto */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Banner de hidratación opcional
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { showIntervalDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WaterDrop, null, tint = Color(0xFF2196F3))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.water_reminder_banner_title), fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                                Text(stringResource(R.string.water_reminder_banner_subtitle, uiState.waterReminderIntervalMinutes), fontSize = 12.sp)
                            }
                        }
                    }

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    query: String, 
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilter: Boolean
) {
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
                contentDescription = stringResource(R.string.food_menu_desc),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { 
                Text(stringResource(R.string.search), color = Color.Gray, fontSize = 14.sp) 
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear), tint = Color.Gray)
                        }
                    }
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.Tune, 
                            contentDescription = stringResource(R.string.nutritional_filters),
                            tint = if (hasActiveFilter) Color(0xFF6750A4) else Color.Gray
                        )
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F0F8),
                unfocusedContainerColor = Color(0xFFF3F0F8),
                disabledContainerColor = Color(0xFFF3F0F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}

@Composable
fun NutritionalFilterContent(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        stringResource(R.string.all),
        stringResource(R.string.low_calories),
        stringResource(R.string.protein_rich),
        stringResource(R.string.saludable)
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.nutritional_filters_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        filters.forEach { filter ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = filter,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedFilter == filter) Color(0xFF6750A4) else Color.Black,
                    fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                )
                if (selectedFilter == filter) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF6750A4))
                }
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun CategorySection(categories: List<Category>, navController: NavController) {
    val otherCategory = stringResource(R.string.otros)
    val displayCategories = categories.filter { it.name != otherCategory }.take(5) +
            (categories.find { it.name == otherCategory } ?: Category(otherCategory, Icons.Default.MoreHoriz))

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
                                if (category.name == otherCategory) {
                                    navController.navigate(Screen.AllCategories.route)
                                } else {
                                    navController.navigate(Screen.Category.createRoute(category.name))
                                }
                            }
                        )
                    }
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
    if (favoriteItems.isEmpty()) return
    
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
    if (tips.isEmpty()) return
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.quick_tips),
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
    if (challenges.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.healthy_challenges),
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
                        text = stringResource(R.string.reward_points, challenge.recompensaPuntos),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4A2B8A)
                    )
                }
            }
        }
    }
}
