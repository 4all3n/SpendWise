package com.fallen.spenwise.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class UserRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val auth = FirebaseAuth.getInstance()

    // Get current user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Check if user exists in local database
    fun userExists(uid: String): Boolean {
        return dbHelper.getUser(uid) != null
    }

    // Add a new user to local database
    fun addUser(uid: String, name: String): Long {
        return dbHelper.addUser(uid, name)
    }

    // Get user details
    fun getUser(uid: String): Map<String, Any>? {
        return dbHelper.getUser(uid)
    }

    // Get user's total balance
    fun getUserTotal(uid: String): Double {
        val user = getUser(uid)
        return user?.get(DatabaseHelper.COLUMN_TOTAL) as? Double ?: 0.0
    }

    // Update user's total balance
    fun updateUserTotal(uid: String, newTotal: Double) {
        dbHelper.updateUserTotal(uid, newTotal)
    }

    // Initialize user in local database if not exists
    fun initializeUserIfNeeded() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val name = currentUser.displayName ?: "User"
            
            if (!userExists(uid)) {
                addUser(uid, name)
            }
        }
    }

    // Get user's name
    fun getUserName(uid: String): String {
        val user = getUser(uid)
        return user?.get(DatabaseHelper.COLUMN_NAME) as? String ?: "User"
    }
} 