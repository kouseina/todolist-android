package com.kouseina.todolist.data.model

data class Category(
    val id: Int = 0,
    val name: String,
    val color: String = "#6200EE",
    val createdAt: Long = System.currentTimeMillis()
)
