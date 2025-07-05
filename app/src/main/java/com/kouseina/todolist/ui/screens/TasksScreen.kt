package com.kouseina.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.kouseina.todolist.data.model.Priority
import com.kouseina.todolist.ui.components.TodoCard
import com.kouseina.todolist.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TodoViewModel,
    onNavigateToEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTodos by viewModel.allTodos.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Filter todos based on selected filters
    val filteredTodos = remember(allTodos, selectedFilter, selectedPriority) {
        var filtered = allTodos

        // Filter by status
        filtered = when (selectedFilter) {
            "Active" -> filtered.filter { !it.isCompleted }
            "Completed" -> filtered.filter { it.isCompleted }
            else -> filtered
        }

        // Filter by priority
        selectedPriority?.let { priority ->
            filtered = filtered.filter { it.priority == priority }
        }

        filtered
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Tasks",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { showFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                onClick = { selectedFilter = "All" },
                label = { Text("All") },
                selected = selectedFilter == "All"
            )
            FilterChip(
                onClick = { selectedFilter = "Active" },
                label = { Text("Active") },
                selected = selectedFilter == "Active"
            )
            FilterChip(
                onClick = { selectedFilter = "Completed" },
                label = { Text("Completed") },
                selected = selectedFilter == "Completed"
            )
        }

        if (selectedPriority != null) {
            Spacer(modifier = Modifier.height(8.dp))
            FilterChip(
                onClick = { selectedPriority = null },
                label = { Text("${selectedPriority!!.name} Priority") },
                selected = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove filter",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tasks count
        Text(
            text = "${filteredTodos.size} tasks",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tasks list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredTodos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredTodos) { todo ->
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

    // Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Priority") },
            text = {
                Column {
                    Priority.values().forEach { priority ->
                        TextButton(
                            onClick = {
                                selectedPriority = if (selectedPriority == priority) null else priority
                                showFilterDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(priority.name)
                                if (selectedPriority == priority) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}
