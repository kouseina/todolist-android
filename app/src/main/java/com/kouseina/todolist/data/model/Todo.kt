package com.kouseina.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
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
