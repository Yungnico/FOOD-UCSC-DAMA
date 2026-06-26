package com.example.food_ucsc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Explore está activo si estamos en la lista, en el detalle de un local o en categorías
    val isExploreActive = currentRoute == Screen.Explore.route || 
                         currentRoute?.startsWith("restaurant") == true ||
                         currentRoute == Screen.AllCategories.route ||
                         currentRoute?.startsWith("category") == true

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
            NavItem(
                label = stringResource(R.string.nav_home),
                icon = Icons.Default.Home,
                isActive = currentRoute == Screen.Home.route
            ) {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            NavItem(
                label = stringResource(R.string.nav_explore),
                icon = Icons.Default.Explore,
                isActive = isExploreActive
            ) {
                if (currentRoute != Screen.Explore.route) {
                    navController.navigate(Screen.Explore.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            NavItem(
                label = "Map",
                icon = Icons.Default.LocationOn,
                isActive = currentRoute == Screen.Map.route
            ) {
                if (currentRoute != Screen.Map.route) {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            NavItem(
                label = stringResource(R.string.nav_profile),
                icon = Icons.Default.Person,
                isActive = currentRoute == Screen.Profile.route
            ) {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

@Composable
fun NavItem(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color)
        Text(text = label, color = color, fontSize = 12.sp)
    }
}
