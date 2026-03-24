package com.toybox.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNav(nav: NavController) {
    val current by nav.currentBackStackEntryAsState()
    val route = current?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = route == "home",
            onClick = { nav.navigate("home") { launchSingleTop = true } }
        )
        BottomNavItem(
            icon = Icons.Default.GridView,
            label = "My toys",
            selected = route == "mytoys",
            onClick = { nav.navigate("mytoys") { launchSingleTop = true } }
        )
        BottomNavItem(
            icon = Icons.Default.AddCircle,
            label = "Add toy",
            selected = route == "addtoy",
            onClick = { nav.navigate("addtoy") { launchSingleTop = true } }
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .then(
                            if (selected) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(8.dp)
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) accent else muted,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (selected) {
                    Spacer(Modifier.height(3.dp))
                    Box(
                        Modifier
                            .size(4.dp)
                            .background(accent, CircleShape)
                    )
                }
            }
        },
        label = {
            Text(
                label,
                fontSize = 9.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = if (selected) accent else muted
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}