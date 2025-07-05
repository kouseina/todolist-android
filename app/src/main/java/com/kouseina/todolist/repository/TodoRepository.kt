package com.kouseina.todolist.repository

import android.content.Context
import com.kouseina.todolist.data.database.SharedPreferencesHelper
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
    private val sharedPreferencesHelper = SharedPreferencesHelper(context)

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

    // SharedPreferences operations for categories
    fun getCustomCategories(): Flow<List<Category>> = flow {
        emit(sharedPreferencesHelper.getCategories())
    }.flowOn(Dispatchers.IO)

    suspend fun addCustomCategory(category: Category) {
        sharedPreferencesHelper.addCategory(category)
    }

    suspend fun updateCustomCategory(category: Category) {
        sharedPreferencesHelper.updateCategory(category)
    }

    suspend fun deleteCustomCategory(category: Category) {
        sharedPreferencesHelper.deleteCategory(category)
    }

    // User preferences
    fun getUserName(): String = sharedPreferencesHelper.getUserInfo().first
    fun getUserNIM(): String = sharedPreferencesHelper.getUserInfo().second

    fun saveUserInfo(name: String, nim: String) {
        sharedPreferencesHelper.saveUserInfo(name, nim)
    }

    fun isDarkMode(): Boolean = sharedPreferencesHelper.isDarkMode()
    fun setDarkMode(isDarkMode: Boolean) {
        sharedPreferencesHelper.setThemeMode(isDarkMode)
    }

    fun isFirstLaunch(): Boolean = sharedPreferencesHelper.isFirstLaunch()
    fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferencesHelper.setFirstLaunch(isFirstLaunch)
    }

    // Clear all data
    fun clearAllData() {
        sharedPreferencesHelper.clearAllData()
        // Note: SQLite database will be cleared when app is uninstalled
    }
}
