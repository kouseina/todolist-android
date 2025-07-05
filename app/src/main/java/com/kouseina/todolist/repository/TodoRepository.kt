package com.kouseina.todolist.repository

import android.content.Context
import com.kouseina.todolist.data.database.TodoDatabase
import com.kouseina.todolist.data.model.Category
import com.kouseina.todolist.data.model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

class TodoRepository(context: Context) {
    private val todoDatabase = TodoDatabase(context)

    fun getAllTodos(): Flow<List<Todo>> = flow {
        emit(todoDatabase.getAllTodos())
    }.flowOn(Dispatchers.IO)

    fun getActiveTodos(): Flow<List<Todo>> = flow {
        emit(todoDatabase.getActiveTodos())
    }.flowOn(Dispatchers.IO)

    fun getCompletedTodos(): Flow<List<Todo>> = flow {
        emit(todoDatabase.getCompletedTodos())
    }.flowOn(Dispatchers.IO)

    fun getTodosByCategory(category: String): Flow<List<Todo>> = flow {
        emit(todoDatabase.getTodosByCategory(category))
    }.flowOn(Dispatchers.IO)

    suspend fun getTodoById(id: Int): Todo? {
        return todoDatabase.getTodoById(id)
    }

    suspend fun insertTodo(
        title: String,
        description: String,
        priority: com.kouseina.todolist.data.model.Priority,
        category: String,
        dueDate: Date?
    ): Long {
        val todo = Todo(
            title = title,
            description = description,
            priority = priority,
            category = category,
            dueDate = dueDate
        )
        return todoDatabase.insertTodo(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        todoDatabase.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDatabase.deleteTodo(todo)
    }

    suspend fun updateTodoStatus(id: Int, isCompleted: Boolean) {
        todoDatabase.updateTodoStatus(id, isCompleted)
    }

    fun getAllCategories(): Flow<List<String>> = flow {
        emit(todoDatabase.getAllCategories())
    }.flowOn(Dispatchers.IO)

    // Category operations
    fun getCustomCategories(): Flow<List<Category>> = flow {
        emit(todoDatabase.getAllCustomCategories())
    }.flowOn(Dispatchers.IO)

    suspend fun addCustomCategory(category: Category) {
        todoDatabase.insertCategory(category)
    }

    suspend fun updateCustomCategory(category: Category) {
        todoDatabase.updateCategory(category)
    }

    suspend fun deleteCustomCategory(category: Category) {
        todoDatabase.deleteCategory(category)
    }

    // User preferences
    fun getUserInfo(): Pair<String, String> = todoDatabase.getUserInfo()
    fun saveUserInfo(name: String, nim: String) = todoDatabase.saveUserInfo(name, nim)
    
    fun isDarkMode(): Boolean = todoDatabase.isDarkMode()
    fun setDarkMode(isDarkMode: Boolean) = todoDatabase.setThemeMode(isDarkMode)
    
    fun isFirstLaunch(): Boolean = todoDatabase.isFirstLaunch()
    fun setFirstLaunch(isFirstLaunch: Boolean) = todoDatabase.setFirstLaunch(isFirstLaunch)

    // Clear all data
    fun clearAllData() {
        // This would require implementing a method to clear all tables
        // For now, we'll leave this empty as SQLite data persists until app uninstall
    }
}
