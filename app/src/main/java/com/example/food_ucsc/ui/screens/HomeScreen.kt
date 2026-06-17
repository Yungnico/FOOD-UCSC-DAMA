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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar() },
        containerColor = Color(0xFFFBF8FF) // Light lavender background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Header()
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(navController = navController)
            Spacer(modifier = Modifier.height(24.dp))
            RecommendedSection()
            Spacer(modifier = Modifier.height(24.dp))
            FavouriteSection()
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
        // Purple logo background on the left
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 64.dp)
                .clip(RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                .background(Color(0xFF6750A4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Search Bar
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
                    imageVector = Icons.Default.Menu,
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
fun CategorySection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.category),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 3 columns x 2 rows grid for better readability
        val categories = listOf(
            stringResource(R.string.comida_rapida) to Icons.Default.Fastfood,
            stringResource(R.string.saludable) to Icons.Default.Restaurant,
            stringResource(R.string.vegetariana) to Icons.Default.Grass,
            stringResource(R.string.vegana) to Icons.Default.Eco,
            stringResource(R.string.postres) to Icons.Default.Cake,
            stringResource(R.string.otros) to Icons.Default.MoreHoriz
        )
        
        Column {
            categories.chunked(3).forEachIndexed { index, rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCategories.forEach { (name, icon) ->
                        CategoryItem(
                            name = name,
                            icon = icon,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.Category.createRoute(name))
                            }
                        )
                    }
                }
                if (index == 0) Spacer(modifier = Modifier.height(16.dp))
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
fun RecommendedSection() {
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
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductCard("T Shirts", Icons.Default.Checkroom)
            ProductCard("Trousers", Icons.Default.Checkroom) // Placeholder icon
            ProductCard("Bag", Icons.Default.ShoppingBag)
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
fun FavouriteSection() {
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
            FavouriteCard(Icons.Default.Laptop)
            FavouriteCard(Icons.Default.Weekend)
            FavouriteCard(Icons.Default.Restaurant)
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
fun BottomNavBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem("Home", Icons.Default.Home, isActive = true)
            NavItem("Explore", Icons.Default.Explore, isActive = false)
            NavItem("Basket", Icons.Default.ShoppingBasket, isActive = false)
            NavItem("Profile", Icons.Default.Person, isActive = false)
        }
    }
}

@Composable
fun NavItem(label: String, icon: ImageVector, isActive: Boolean) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = color)
        Text(text = label, color = color, fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Para el preview, podemos usar un NavController falso o simplemente no pasar nada
    // pero como HomeScreen ahora lo requiere, usaremos uno básico
    // HomeScreen(navController = rememberNavController())
}
