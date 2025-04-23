package com.fallen.spenwise.data

import android.content.Context
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.*

class BudgetRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Add a new budget
    fun addBudget(uid: String, category: String, limit: Double, startDate: Date, endDate: Date): Long {
        val startDateString = dateFormatter.format(startDate)
        val endDateString = dateFormatter.format(endDate)
        return dbHelper.addBudget(uid, category, limit, startDateString, endDateString)
    }

    // Get all budgets for a user
    fun getBudgets(uid: String): List<Map<String, Any>> {
        return dbHelper.getBudgets(uid)
    }

    // Get active budgets (current date is between start and end date)
    fun getActiveBudgets(uid: String): List<Map<String, Any>> {
        val currentDate = dateFormatter.format(Date())
        val allBudgets = getBudgets(uid)
        
        return allBudgets.filter { budget ->
            val startDate = budget[DatabaseHelper.COLUMN_START_DATE] as String
            val endDate = budget[DatabaseHelper.COLUMN_END_DATE] as String
            currentDate in startDate..endDate
        }
    }

    // Get budget by category
    fun getBudgetByCategory(uid: String, category: String): Map<String, Any>? {
        val budgets = getBudgets(uid)
        return budgets.find { it[DatabaseHelper.COLUMN_CATEGORY] == category }
    }

    // Get active budget by category
    fun getActiveBudgetByCategory(uid: String, category: String): Map<String, Any>? {
        val activeBudgets = getActiveBudgets(uid)
        return activeBudgets.find { it[DatabaseHelper.COLUMN_CATEGORY] == category }
    }

    // Get budget usage (spent amount) for a category
    fun getBudgetUsage(uid: String, category: String): Double {
        val transactionRepo = TransactionRepository(dbHelper.context)
        val expenses = transactionRepo.getExpensesByCategory(uid)
        return expenses[category] ?: 0.0
    }

    // Get budget remaining amount for a category
    fun getBudgetRemaining(uid: String, category: String): Double {
        val budget = getActiveBudgetByCategory(uid, category)
        if (budget != null) {
            val limit = budget[DatabaseHelper.COLUMN_LIMIT] as Double
            val usage = getBudgetUsage(uid, category)
            return limit - usage
        }
        return 0.0
    }

    // Get budget progress percentage for a category
    fun getBudgetProgress(uid: String, category: String): Int {
        val budget = getActiveBudgetByCategory(uid, category)
        if (budget != null) {
            val limit = budget[DatabaseHelper.COLUMN_LIMIT] as Double
            val usage = getBudgetUsage(uid, category)
            return if (limit > 0) ((usage / limit) * 100).toInt() else 0
        }
        return 0
    }

    // Delete a budget by category
    fun deleteBudget(uid: String, category: String): Boolean {
        return dbHelper.deleteBudget(uid, category)
    }

    // Update a budget's limit
    fun updateBudget(uid: String, category: String, newLimit: Double): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_LIMIT, newLimit)
        
        val whereClause = "${DatabaseHelper.COLUMN_UID} = ? AND ${DatabaseHelper.COLUMN_CATEGORY} = ?"
        val whereArgs = arrayOf(uid, category)
        
        val result = db.update(DatabaseHelper.TABLE_BUDGET, values, whereClause, whereArgs)
        return result > 0
    }

    // Check if a budget is exceeded for a category
    fun isBudgetExceeded(uid: String, category: String): Boolean {
        val remaining = getBudgetRemaining(uid, category)
        return remaining < 0
    }
} 