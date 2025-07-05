package com.kouseina.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.kouseina.todolist.ui.components.TodoCard
import com.kouseina.todolist.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTasksScreen(
    category: String,
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTodos by viewModel.allTodos.collectAsState(initial = emptyList())

    val categoryTodos = remember(allTodos, category) {
        allTodos.filter { it.category == category }
    }

    val activeTodos = categoryTodos.filter { !it.isCompleted }
    val completedTodos = categoryTodos.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = category,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${categoryTodos.size} tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Statistics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Active",
                count = activeTodos.size,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Completed",
                count = completedTodos.size,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (categoryTodos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks in this category yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeTodos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Active Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(activeTodos) { todo ->
                        TodoCard(
                            todo = todo,
                            onToggleComplete = { viewModel.toggleTodoStatus(todo) },
                            onEdit = { onNavigateToEdit(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo) }
                        )
                    }
                }

                if (completedTodos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Completed Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(completedTodos) { todo ->
                        TodoCard(
                            todo = todo,
                            onToggleComplete = { viewModel.toggleTodoStatus(todo) },
                            onEdit = { onNavigateToEdit(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}
