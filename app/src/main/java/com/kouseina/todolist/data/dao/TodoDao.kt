package com.kouseina.todolist.data.dao

import androidx.room.*
import com.kouseina.todolist.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY priority DESC, dueDate ASC")
    fun getActiveTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE category = :category ORDER BY createdAt DESC")
    fun getTodosByCategory(category: String): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Int): Todo?

    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("UPDATE todos SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoStatus(id: Int, isCompleted: Boolean, updatedAt: java.util.Date)

    @Query("SELECT DISTINCT category FROM todos ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
}
