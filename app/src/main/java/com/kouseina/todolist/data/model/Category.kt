package com.kouseina.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String = "#6200EE",
    val createdAt: Long = System.currentTimeMillis()
)
