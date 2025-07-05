package com.kouseina.todolist.data.database

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kouseina.todolist.data.model.Category

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "todo_preferences"
        
        // Keys for preferences
        private const val KEY_CATEGORIES = "categories"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_NIM = "user_nim"
    }

    // Categories management
    fun saveCategories(categories: List<Category>) {
        val json = gson.toJson(categories)
        sharedPreferences.edit().putString(KEY_CATEGORIES, json).apply()
    }

    fun getCategories(): List<Category> {
        val json = sharedPreferences.getString(KEY_CATEGORIES, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<Category>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun addCategory(category: Category) {
        val categories = getCategories().toMutableList()
        categories.add(category.copy(id = if (categories.isEmpty()) 1 else categories.maxOf { it.id } + 1))
        saveCategories(categories)
    }

    fun updateCategory(category: Category) {
        val categories = getCategories().toMutableList()
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            categories[index] = category
            saveCategories(categories)
        }
    }

    fun deleteCategory(category: Category) {
        val categories = getCategories().toMutableList()
        categories.removeAll { it.id == category.id }
        saveCategories(categories)
    }

    // Theme preferences
    fun setThemeMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_THEME_MODE, isDarkMode).apply()
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_THEME_MODE, false)
    }

    // First launch flag
    fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    // User info
    fun saveUserInfo(name: String, nim: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_NIM, nim)
            .apply()
    }

    fun getUserInfo(): Pair<String, String> {
        val name = sharedPreferences.getString(KEY_USER_NAME, "User") ?: "User"
        val nim = sharedPreferences.getString(KEY_USER_NIM, "NIM") ?: "NIM"
        return Pair(name, nim)
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "User") ?: "User"
    }

    fun getUserNIM(): String {
        return sharedPreferences.getString(KEY_USER_NIM, "NIM") ?: "NIM"
    }

    // Clear all data
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
} 