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
    
    private val _allTodos = MutableStateFlow<List<Todo>>(emptyList())
    val allTodos: StateFlow<List<Todo>> = _allTodos.asStateFlow()
    
    private val _activeTodos = MutableStateFlow<List<Todo>>(emptyList())
    val activeTodos: StateFlow<List<Todo>> = _activeTodos.asStateFlow()
    
    private val _completedTodos = MutableStateFlow<List<Todo>>(emptyList())
    val completedTodos: StateFlow<List<Todo>> = _completedTodos.asStateFlow()
    
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    
    private val _selectedTodo = MutableStateFlow<Todo?>(null)
    val selectedTodo: StateFlow<Todo?> = _selectedTodo.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshTodos()
    }

    fun refreshTodos() {
        viewModelScope.launch {
            repository.getAllTodos().collect { todos ->
                _allTodos.value = todos
                _activeTodos.value = todos.filter { !it.isCompleted }
                _completedTodos.value = todos.filter { it.isCompleted }
            }
        }
        viewModelScope.launch {
            repository.getAllCategories().collect { cats ->
                _categories.value = cats
            }
        }
    }
    
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
                refreshTodos()
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
                refreshTodos()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
            refreshTodos()
        }
    }
    
    fun toggleTodoStatus(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodoStatus(todo.id, !todo.isCompleted)
            refreshTodos()
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
    fun getUserInfo(): Pair<String, String> = repository.getUserInfo()
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
