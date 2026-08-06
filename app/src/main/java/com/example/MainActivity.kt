package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.YtSeoViewModel
import com.example.ui.screens.AuditResultScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.SavedAuditsScreen
import com.example.ui.theme.YtRedPrimary
import com.example.ui.theme.YtSeoTheme

sealed class BottomNavItem(
    val route: String,
    val titleBn: String,
    val icon: ImageVector
) {
    object Generator : BottomNavItem("generator", "অডিট", Icons.Default.Analytics)
    object Result : BottomNavItem("result", "ফলাফল", Icons.Default.CheckCircle)
    object Saved : BottomNavItem("saved", "সংরক্ষিত", Icons.Default.Bookmark)
    object Guide : BottomNavItem("guide", "গাইড", Icons.Default.Lightbulb)
}

class MainActivity : ComponentActivity() {
    private val viewModel: YtSeoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            YtSeoTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: YtSeoViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Generator.route

    val navItems = listOf(
        BottomNavItem.Generator,
        BottomNavItem.Result,
        BottomNavItem.Saved,
        BottomNavItem.Guide
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.titleBn,
                                tint = if (isSelected) YtRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = item.titleBn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) YtRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = YtRedPrimary.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("nav_item_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Generator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Generator.route) {
                GeneratorScreen(
                    viewModel = viewModel,
                    onNavigateToResult = {
                        navController.navigate(BottomNavItem.Result.route)
                    }
                )
            }

            composable(BottomNavItem.Result.route) {
                AuditResultScreen(
                    viewModel = viewModel,
                    onNavigateToGenerator = {
                        navController.navigate(BottomNavItem.Generator.route)
                    }
                )
            }

            composable(BottomNavItem.Saved.route) {
                SavedAuditsScreen(
                    viewModel = viewModel,
                    onSelectAudit = {
                        navController.navigate(BottomNavItem.Result.route)
                    }
                )
            }

            composable(BottomNavItem.Guide.route) {
                GuideScreen()
            }
        }
    }
}
