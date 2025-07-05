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
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop all tables and recreate
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS user_preferences")
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TODOS)
        db.execSQL(CREATE_TABLE_CATEGORIES)
        
        // Create user_preferences table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS user_preferences (
                id INTEGER PRIMARY KEY,
                user_name TEXT,
                user_nim TEXT,
                theme_mode INTEGER DEFAULT 0,
                first_launch INTEGER DEFAULT 1
            )
        """)
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
        
        try {
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
        } catch (e: Exception) {
            // If table doesn't exist or has wrong structure, return empty list
            android.util.Log.e("TodoDatabase", "Error getting all todos: ${e.message}")
        }
        
        return todos
    }

    fun getActiveTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = this.readableDatabase
        
        try {
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
        } catch (e: Exception) {
            // If table doesn't exist or has wrong structure, return empty list
            android.util.Log.e("TodoDatabase", "Error getting active todos: ${e.message}")
        }
        
        return todos
    }

    fun getCompletedTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = this.readableDatabase
        
        try {
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
        } catch (e: Exception) {
            // If table doesn't exist or has wrong structure, return empty list
            android.util.Log.e("TodoDatabase", "Error getting completed todos: ${e.message}")
        }
        
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
        
        try {
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
        } catch (e: Exception) {
            // If table doesn't exist or has wrong structure, return default categories
            android.util.Log.e("TodoDatabase", "Error getting categories: ${e.message}")
            categories.addAll(listOf("Umum", "Kerja", "Pribadi", "Belanja", "Kesehatan"))
        }
        
        return categories
    }

    // Category operations
    fun insertCategory(category: Category): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, category.name)
            put(KEY_COLOR, category.color)
            put(KEY_CREATED_AT, category.createdAt)
        }
        return db.insert(TABLE_CATEGORIES, null, values)
    }

    fun updateCategory(category: Category): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, category.name)
            put(KEY_COLOR, category.color)
        }
        return db.update(TABLE_CATEGORIES, values, "$KEY_ID = ?", arrayOf(category.id.toString()))
    }

    fun deleteCategory(category: Category): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_CATEGORIES, "$KEY_ID = ?", arrayOf(category.id.toString()))
    }

    fun getAllCustomCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIES,
            null,
            null,
            null,
            null,
            null,
            "$KEY_NAME ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursorToCategory(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return categories
    }

    private fun cursorToCategory(cursor: android.database.Cursor): Category {
        return Category(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
            color = cursor.getString(cursor.getColumnIndexOrThrow(KEY_COLOR)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CREATED_AT))
        )
    }

    // User preferences operations
    fun saveUserInfo(name: String, nim: String) {
        val db = this.writableDatabase
        
        // Insert or update user info
        val values = ContentValues().apply {
            put("user_name", name)
            put("user_nim", nim)
        }
        
        // Check if user info exists
        val cursor = db.query("user_preferences", null, null, null, null, null, null)
        if (cursor.count > 0) {
            db.update("user_preferences", values, "id = 1", null)
        } else {
            values.put("id", 1)
            db.insert("user_preferences", null, values)
        }
        cursor.close()
    }

    fun getUserInfo(): Pair<String, String> {
        val db = this.readableDatabase
        
        try {
            val cursor = db.query("user_preferences", null, "id = 1", null, null, null, null)
            
            return if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("user_name")) ?: "User"
                val nim = cursor.getString(cursor.getColumnIndexOrThrow("user_nim")) ?: "NIM"
                cursor.close()
                Pair(name, nim)
            } else {
                cursor.close()
                Pair("User", "NIM")
            }
        } catch (e: Exception) {
            // If table doesn't exist, return default values
            android.util.Log.e("TodoDatabase", "Error getting user info: ${e.message}")
            return Pair("User", "NIM")
        }
    }

    fun setThemeMode(isDarkMode: Boolean) {
        val db = this.writableDatabase
        
        try {
            val values = ContentValues().apply {
                put("theme_mode", if (isDarkMode) 1 else 0)
            }
            
            val cursor = db.query("user_preferences", null, null, null, null, null, null)
            if (cursor.count > 0) {
                db.update("user_preferences", values, "id = 1", null)
            } else {
                values.put("id", 1)
                db.insert("user_preferences", null, values)
            }
            cursor.close()
        } catch (e: Exception) {
            android.util.Log.e("TodoDatabase", "Error setting theme mode: ${e.message}")
        }
    }

    fun isDarkMode(): Boolean {
        val db = this.readableDatabase
        
        try {
            val cursor = db.query("user_preferences", arrayOf("theme_mode"), "id = 1", null, null, null, null)
            
            return if (cursor.moveToFirst()) {
                val isDark = cursor.getInt(cursor.getColumnIndexOrThrow("theme_mode")) == 1
                cursor.close()
                isDark
            } else {
                cursor.close()
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("TodoDatabase", "Error checking dark mode: ${e.message}")
            return false
        }
    }

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        val db = this.writableDatabase
        
        try {
            val values = ContentValues().apply {
                put("first_launch", if (isFirstLaunch) 1 else 0)
            }
            
            val cursor = db.query("user_preferences", null, null, null, null, null, null)
            if (cursor.count > 0) {
                db.update("user_preferences", values, "id = 1", null)
            } else {
                values.put("id", 1)
                db.insert("user_preferences", null, values)
            }
            cursor.close()
        } catch (e: Exception) {
            android.util.Log.e("TodoDatabase", "Error setting first launch: ${e.message}")
        }
    }

    fun isFirstLaunch(): Boolean {
        val db = this.readableDatabase
        
        try {
            val cursor = db.query("user_preferences", arrayOf("first_launch"), "id = 1", null, null, null, null)
            
            return if (cursor.moveToFirst()) {
                val isFirst = cursor.getInt(cursor.getColumnIndexOrThrow("first_launch")) == 1
                cursor.close()
                isFirst
            } else {
                cursor.close()
                true
            }
        } catch (e: Exception) {
            // If table doesn't exist, it's first launch
            android.util.Log.e("TodoDatabase", "Error checking first launch: ${e.message}")
            return true
        }
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
