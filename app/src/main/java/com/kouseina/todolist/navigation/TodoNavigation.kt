package com.kouseina.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.kouseina.todolist.ui.screens.*
import com.kouseina.todolist.viewmodel.TodoViewModel

@Composable
fun TodoNavigation(
    navController: NavHostController,
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = {
                    navController.navigate("add_edit")
                },
                onNavigateToEdit = { todoId ->
                    navController.navigate("add_edit/$todoId")
                }
            )
        }
        
        composable("tasks") {
            TasksScreen(
                viewModel = viewModel,
                onNavigateToEdit = { todoId ->
                    navController.navigate("add_edit/$todoId")
                }
            )
        }
        
        composable("categories") {
            CategoriesScreen(
                viewModel = viewModel,
                onNavigateToTasks = { category ->
                    navController.navigate("category_tasks/$category")
                }
            )
        }
        
        composable("statistics") {
            StatisticsScreen(viewModel = viewModel)
        }
        
        composable("add_edit") {
            AddEditTodoScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            "add_edit/{todoId}",
            arguments = listOf(navArgument("todoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId")
            AddEditTodoScreen(
                viewModel = viewModel,
                todoId = todoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            "category_tasks/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryTasksScreen(
                category = category,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { todoId ->
                    navController.navigate("add_edit/$todoId")
                }
            )
        }
    }
}
