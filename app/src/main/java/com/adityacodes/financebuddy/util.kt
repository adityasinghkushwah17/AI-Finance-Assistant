package com.adityacodes.financebuddy

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Chat", "chat", Icons.Default.Chat),
        BottomNavItem("Transactions", "transactions", Icons.Default.MonetizationOn),
        BottomNavItem("Goals", "goals", Icons.Default.Accessibility)
    )
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFE7F4F7), // matches the chat screen background
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color(0xFF41B8C9) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) Color(0xFF41B8C9) else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)
