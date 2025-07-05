package com.kouseina.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kouseina.todolist.R
import com.kouseina.todolist.data.model.Priority
import com.kouseina.todolist.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoScreen(
    viewModel: TodoViewModel,
    todoId: Int? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf("General") }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val selectedTodo by viewModel.selectedTodo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    val isEditing = todoId != null

    // Load todo for editing
    LaunchedEffect(todoId) {
        if (todoId != null) {
            viewModel.getTodoById(todoId)
        }
    }

    // Populate fields when editing
    LaunchedEffect(selectedTodo) {
        selectedTodo?.let { todo ->
            title = todo.title
            description = todo.description
            selectedPriority = todo.priority
            selectedCategory = todo.category
            dueDate = todo.dueDate
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }

            Text(
                text = if (isEditing) stringResource(R.string.edit_task) else stringResource(R.string.add_new_task),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        if (isEditing && selectedTodo != null) {
                            viewModel.updateTodo(
                                selectedTodo!!.copy(
                                    title = title,
                                    description = description,
                                    priority = selectedPriority,
                                    category = selectedCategory,
                                    dueDate = dueDate
                                )
                            )
                        } else {
                            viewModel.insertTodo(
                                title = title,
                                description = description,
                                priority = selectedPriority,
                                category = selectedCategory,
                                dueDate = dueDate
                            )
                        }
                        onNavigateBack()
                    }
                },
                enabled = title.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title Field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.task_title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = null
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description Field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description_optional)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Priority Selection
        Text(
            text = stringResource(R.string.priority),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Priority.values().forEach { priority ->
                            FilterChip(
                onClick = { selectedPriority = priority },
                label = { 
                    Text(
                        when (priority) {
                            Priority.LOW -> stringResource(R.string.priority_low)
                            Priority.MEDIUM -> stringResource(R.string.priority_medium)
                            Priority.HIGH -> stringResource(R.string.priority_high)
                            Priority.URGENT -> stringResource(R.string.priority_urgent)
                        }
                    ) 
                },
                selected = selectedPriority == priority,
                modifier = Modifier.weight(1f)
            )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selection
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = { },
            label = { Text(stringResource(R.string.category)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showCategoryDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.cd_select_category)
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Due Date Selection
        OutlinedTextField(
            value = dueDate?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
            } ?: "",
            onValueChange = { },
            label = { Text(stringResource(R.string.due_date_optional)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Row {
                    if (dueDate != null) {
                        IconButton(onClick = { dueDate = null }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.cd_clear_date)
                            )
                        }
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.cd_select_date)
                        )
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null
                )
            }
        )
    }

    // Category Selection Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text(stringResource(R.string.select_category)) },
            text = {
                Column {
                    val availableCategories = listOf(
                        stringResource(R.string.category_general),
                        stringResource(R.string.category_work),
                        stringResource(R.string.category_personal),
                        stringResource(R.string.category_shopping),
                        stringResource(R.string.category_health)
                    ) + categories
                    availableCategories.distinct().forEach { category ->
                        TextButton(
                            onClick = {
                                selectedCategory = category
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Date Picker Dialog (Simple implementation)
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text(stringResource(R.string.select_due_date)) },
            text = {
                Column {
                    Text(stringResource(R.string.select_date_for_task))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                dueDate = Calendar.getInstance().apply {
                                    add(Calendar.DAY_OF_MONTH, 1)
                                }.time
                                showDatePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.tomorrow))
                        }
                        TextButton(
                            onClick = {
                                dueDate = Calendar.getInstance().apply {
                                    add(Calendar.DAY_OF_MONTH, 7)
                                }.time
                                showDatePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.next_week))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDate = Calendar.getInstance().time
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.today))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
