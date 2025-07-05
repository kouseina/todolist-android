package com.kouseina.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kouseina.todolist.data.database.TodoDatabase
import com.kouseina.todolist.navigation.TodoNavigation
import com.kouseina.todolist.repository.TodoRepository
import com.kouseina.todolist.ui.theme.ToDoListTheme
import com.kouseina.todolist.viewmodel.TodoViewModel
import com.kouseina.todolist.viewmodel.TodoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                TodoApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Initialize database and repository
    val database = TodoDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
    val repository = TodoRepository(database.todoDao())
    val viewModel: TodoViewModel = viewModel(
        factory = TodoViewModelFactory(repository)
    )

    // Define bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem("home", stringResource(R.string.nav_home), Icons.Default.Home),
        BottomNavItem("tasks", stringResource(R.string.nav_tasks), Icons.Default.Assignment),
        BottomNavItem("categories", stringResource(R.string.nav_categories), Icons.Default.Category),
        BottomNavItem("statistics", stringResource(R.string.nav_statistics), Icons.Default.Analytics)
    )

    // Check if current route should show bottom navigation
    val showBottomNav = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
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
        }
    ) { paddingValues ->
        TodoNavigation(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
