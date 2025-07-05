package com.kouseina.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kouseina.todolist.data.model.Priority
import com.kouseina.todolist.data.model.Todo
import com.kouseina.todolist.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    
    val allTodos = repository.getAllTodos()
    val activeTodos = repository.getActiveTodos()
    val completedTodos = repository.getCompletedTodos()
    val categories = repository.getAllCategories()
    
    private val _selectedTodo = MutableStateFlow<Todo?>(null)
    val selectedTodo: StateFlow<Todo?> = _selectedTodo.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun insertTodo(
        title: String,
        description: String,
        priority: Priority,
        category: String,
        dueDate: Date?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insertTodo(title, description, priority, category, dueDate)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateTodo(todo.copy(updatedAt = Date()))
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }
    
    fun toggleTodoStatus(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodoStatus(todo.id, !todo.isCompleted)
        }
    }
    
    fun getTodoById(id: Int) {
        viewModelScope.launch {
            _selectedTodo.value = repository.getTodoById(id)
        }
    }
    
    fun clearSelectedTodo() {
        _selectedTodo.value = null
    }

    // User preferences
    fun getUserName(): String = repository.getUserName()
    fun getUserNIM(): String = repository.getUserNIM()
    fun saveUserInfo(name: String, nim: String) = repository.saveUserInfo(name, nim)
    
    fun isDarkMode(): Boolean = repository.isDarkMode()
    fun setDarkMode(isDarkMode: Boolean) = repository.setDarkMode(isDarkMode)
    
    fun isFirstLaunch(): Boolean = repository.isFirstLaunch()
    fun setFirstLaunch(isFirstLaunch: Boolean) = repository.setFirstLaunch(isFirstLaunch)
}

class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
