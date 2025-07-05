package com.kouseina.todolist.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.kouseina.todolist.data.dao.CategoryDao
import com.kouseina.todolist.data.dao.TodoDao
import com.kouseina.todolist.data.model.Category
import com.kouseina.todolist.data.model.Todo

@Database(
    entities = [Todo::class, Category::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
