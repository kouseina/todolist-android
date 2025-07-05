package com.kouseina.todolist.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kouseina.todolist.data.model.Category
import com.kouseina.todolist.data.model.Priority
import com.kouseina.todolist.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

class TodoDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todo_database"
        private const val DATABASE_VERSION = 1

        // Table names
        private const val TABLE_TODOS = "todos"
        private const val TABLE_CATEGORIES = "categories"

        // Common column names
        private const val KEY_ID = "id"
        private const val KEY_CREATED_AT = "created_at"

        // Todos table column names
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IS_COMPLETED = "is_completed"
        private const val KEY_PRIORITY = "priority"
        private const val KEY_CATEGORY = "category"
        private const val KEY_DUE_DATE = "due_date"
        private const val KEY_UPDATED_AT = "updated_at"

        // Categories table column names
        private const val KEY_NAME = "name"
        private const val KEY_COLOR = "color"

        // Create table statements
        private const val CREATE_TABLE_TODOS = """
            CREATE TABLE $TABLE_TODOS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_TITLE TEXT NOT NULL,
                $KEY_DESCRIPTION TEXT,
                $KEY_IS_COMPLETED INTEGER DEFAULT 0,
                $KEY_PRIORITY TEXT DEFAULT 'MEDIUM',
                $KEY_CATEGORY TEXT DEFAULT 'General',
                $KEY_DUE_DATE TEXT,
                $KEY_CREATED_AT TEXT NOT NULL,
                $KEY_UPDATED_AT TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_CATEGORIES = """
            CREATE TABLE $TABLE_CATEGORIES (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NAME TEXT NOT NULL UNIQUE,
                $KEY_COLOR TEXT DEFAULT '#6200EE',
                $KEY_CREATED_AT INTEGER NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TODOS)
        db.execSQL(CREATE_TABLE_CATEGORIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

    // Todo operations
    fun insertTodo(todo: Todo): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITLE, todo.title)
            put(KEY_DESCRIPTION, todo.description)
            put(KEY_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
            put(KEY_PRIORITY, todo.priority.name)
            put(KEY_CATEGORY, todo.category)
            put(KEY_DUE_DATE, todo.dueDate?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it) })
            put(KEY_CREATED_AT, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(todo.createdAt))
            put(KEY_UPDATED_AT, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(todo.updatedAt))
        }
        return db.insert(TABLE_TODOS, null, values)
    }

    fun updateTodo(todo: Todo): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITLE, todo.title)
            put(KEY_DESCRIPTION, todo.description)
            put(KEY_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
            put(KEY_PRIORITY, todo.priority.name)
            put(KEY_CATEGORY, todo.category)
            put(KEY_DUE_DATE, todo.dueDate?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it) })
            put(KEY_UPDATED_AT, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(todo.updatedAt))
        }
        return db.update(TABLE_TODOS, values, "$KEY_ID = ?", arrayOf(todo.id.toString()))
    }

    fun deleteTodo(todo: Todo): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_TODOS, "$KEY_ID = ?", arrayOf(todo.id.toString()))
    }

    fun getTodoById(id: Int): Todo? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            null,
            "$KEY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val todo = cursorToTodo(cursor)
            cursor.close()
            todo
        } else {
            cursor.close()
            null
        }
    }

    fun getAllTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            null,
            null,
            null,
            null,
            null,
            "$KEY_CREATED_AT DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                todos.add(cursorToTodo(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todos
    }

    fun getActiveTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            null,
            "$KEY_IS_COMPLETED = 0",
            null,
            null,
            null,
            "$KEY_PRIORITY DESC, $KEY_DUE_DATE ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                todos.add(cursorToTodo(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todos
    }

    fun getCompletedTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            null,
            "$KEY_IS_COMPLETED = 1",
            null,
            null,
            null,
            "$KEY_UPDATED_AT DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                todos.add(cursorToTodo(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todos
    }

    fun getTodosByCategory(category: String): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            null,
            "$KEY_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            "$KEY_CREATED_AT DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                todos.add(cursorToTodo(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todos
    }

    fun updateTodoStatus(id: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_IS_COMPLETED, if (isCompleted) 1 else 0)
            put(KEY_UPDATED_AT, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }
        db.update(TABLE_TODOS, values, "$KEY_ID = ?", arrayOf(id.toString()))
    }

    fun getAllCategories(): List<String> {
        val categories = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TODOS,
            arrayOf("DISTINCT $KEY_CATEGORY"),
            null,
            null,
            null,
            null,
            "$KEY_CATEGORY ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return categories
    }

    private fun cursorToTodo(cursor: android.database.Cursor): Todo {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        return Todo(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_COMPLETED)) == 1,
            priority = Priority.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRIORITY))),
            category = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)),
            dueDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DUE_DATE))?.let { 
                try { dateFormat.parse(it) } catch (e: Exception) { null }
            },
            createdAt = try { dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT))) } catch (e: Exception) { Date() },
            updatedAt = try { dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT))) } catch (e: Exception) { Date() }
        )
    }
}
