package com.kouseina.todolist.data.database

import android.content.Context
import com.kouseina.todolist.data.model.Category
import com.kouseina.todolist.repository.TodoRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

object DataInitializer {
    
    fun initializeDefaultData(context: Context) {
        val repository = TodoRepository(context)
        
        // Initialize user info if first launch
        if (repository.isFirstLaunch()) {
            repository.saveUserInfo("Daffa Putera Kouseina", "STI202303773")
            repository.setFirstLaunch(false)
        }
        
        // Initialize default categories if none exist
        runBlocking {
            val existingCategories = repository.getCustomCategories().first()
            if (existingCategories.isEmpty()) {
                val defaultCategories = listOf(
                    Category(name = "Umum", color = "#6200EE"),
                    Category(name = "Kerja", color = "#1976D2"),
                    Category(name = "Pribadi", color = "#388E3C"),
                    Category(name = "Belanja", color = "#F57C00"),
                    Category(name = "Kesehatan", color = "#D32F2F")
                )
                
                defaultCategories.forEach { category ->
                    repository.addCustomCategory(category)
                }
            }
        }
    }
} 