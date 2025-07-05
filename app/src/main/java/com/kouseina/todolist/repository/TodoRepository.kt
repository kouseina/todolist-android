package com.kouseina.todolist.repository

import com.kouseina.todolist.data.dao.TodoDao
import com.kouseina.todolist.data.model.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TodoRepository(private val todoDao: TodoDao) {
    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()
    
    fun getActiveTodos(): Flow<List<Todo>> = todoDao.getActiveTodos()
    
    fun getCompletedTodos(): Flow<List<Todo>> = todoDao.getCompletedTodos()
    
    fun getTodosByCategory(category: String): Flow<List<Todo>> = todoDao.getTodosByCategory(category)
    
    suspend fun getTodoById(id: Int): Todo? = todoDao.getTodoById(id)
    
    suspend fun insertTodo(todo: Todo): Long = todoDao.insertTodo(todo)
    
    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)
    
    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)
    
    suspend fun updateTodoStatus(id: Int, isCompleted: Boolean) {
        todoDao.updateTodoStatus(id, isCompleted, Date())
    }
    
    fun getAllCategories(): Flow<List<String>> = todoDao.getAllCategories()
}
