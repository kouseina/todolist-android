package com.kouseina.todolist.data.model

import java.util.Date

data class Todo(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General",
    val dueDate: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}
