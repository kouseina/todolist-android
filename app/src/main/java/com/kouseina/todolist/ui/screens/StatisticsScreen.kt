package com.kouseina.todolist.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.kouseina.todolist.data.model.Priority
import com.kouseina.todolist.viewmodel.TodoViewModel

@Composable
fun StatisticsScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val allTodos by viewModel.allTodos.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    val completedTodos = allTodos.filter { it.isCompleted }
    val activeTodos = allTodos.filter { !it.isCompleted }
    val overdueTodos = allTodos.filter { todo ->
        !todo.isCompleted && todo.dueDate != null &&
                todo.dueDate!!.before(java.util.Date())
    }

    // Priority statistics
    val priorityStats = Priority.values().map { priority ->
        val count = allTodos.count { it.priority == priority }
        priority to count
    }

    // Category statistics
    val categoryStats = remember(allTodos, categories) {
        val allCategories = (categories + allTodos.map { it.category }).distinct()
        allCategories.map { category ->
            val categoryTodos = allTodos.filter { it.category == category }
            Triple(category, categoryTodos.size, categoryTodos.count { !it.isCompleted })
        }.sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Overview Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverviewCard(
                    title = "Total Tasks",
                    value = allTodos.size.toString(),
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                OverviewCard(
                    title = "Completed",
                    value = completedTodos.size.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverviewCard(
                    title = "Active",
                    value = activeTodos.size.toString(),
                    icon = Icons.Default.PendingActions,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                OverviewCard(
                    title = "Overdue",
                    value = overdueTodos.size.toString(),
                    icon = Icons.Default.Warning,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Completion Rate
        if (allTodos.isNotEmpty()) {
            item {
                val completionRate = (completedTodos.size.toFloat() / allTodos.size * 100).toInt()
                CompletionRateCard(completionRate)
            }
        }

        // Priority Distribution
        if (allTodos.isNotEmpty()) {
            item {
                PriorityDistributionCard(priorityStats)
            }
        }

        // Category Statistics
        if (categoryStats.isNotEmpty()) {
            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(categoryStats.take(5)) { (category, total, active) ->
                CategoryStatCard(
                    category = category,
                    totalTasks = total,
                    activeTasks = active
                )
            }
        }

        // Recent Activity Summary
//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Quick Insights",
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        item {
//            QuickInsightsCard(
//                totalTasks = allTodos.size,
//                completedTasks = completedTodos.size,
//                overdueTasks = overdueTodos.size,
//                categoriesCount = categoryStats.size
//            )
//        }
    }
}

@Composable
private fun OverviewCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Composable
private fun CompletionRateCard(completionRate: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Completion Rate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCompletionCircle(completionRate)
                    }
                    Text(
                        text = "$completionRate%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = "Great progress!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Keep up the good work",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityDistributionCard(priorityStats: List<Pair<Priority, Int>>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Priority Distribution",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            priorityStats.forEach { (priority, count) ->
                if (count > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(getPriorityColor(priority))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = priority.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryStatCard(
    category: String,
    totalTasks: Int,
    activeTasks: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$activeTasks active of $totalTasks total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val progress = if (totalTasks > 0) (totalTasks - activeTasks).toFloat() / totalTasks else 0f
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(40.dp),
                strokeWidth = 4.dp
            )
        }
    }
}

@Composable
private fun QuickInsightsCard(
    totalTasks: Int,
    completedTasks: Int,
    overdueTasks: Int,
    categoriesCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Quick Insights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val insights = buildList {
                if (totalTasks == 0) {
                    add("Start by creating your first task!")
                } else {
                    if (completedTasks == totalTasks) {
                        add("🎉 All tasks completed! Great job!")
                    } else if (overdueTasks > 0) {
                        add("⚠️ You have $overdueTasks overdue task${if (overdueTasks > 1) "s" else ""}")
                    } else {
                        add("✅ No overdue tasks - you're on track!")
                    }

                    if (categoriesCount > 1) {
                        add("📁 Tasks organized in $categoriesCount categories")
                    }

                    val completionRate = (completedTasks.toFloat() / totalTasks * 100).toInt()
                    when {
                        completionRate >= 80 -> add("🔥 Excellent completion rate: $completionRate%")
                        completionRate >= 60 -> add("👍 Good completion rate: $completionRate%")
                        else -> add("💪 Room for improvement: $completionRate% completion rate")
                    }
                }
            }

            insights.forEach { insight ->
                Text(
                    text = insight,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawCompletionCircle(completionRate: Int) {
    val strokeWidth = 8.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    // Background circle
    drawCircle(
        color = Color.Gray.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
    )

    // Progress arc
    val sweepAngle = (completionRate / 100f) * 360f
    drawArc(
        color = Color(0xFF4CAF50),
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
    )
}

@Composable
private fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.LOW -> Color(0xFF4CAF50)
        Priority.MEDIUM -> Color(0xFFFF9800)
        Priority.HIGH -> Color(0xFFFF5722)
        Priority.URGENT -> Color(0xFFF44336)
    }
}
