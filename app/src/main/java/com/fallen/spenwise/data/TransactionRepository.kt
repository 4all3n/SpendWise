package com.fallen.spenwise.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_AMOUNT
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_CATEGORY
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_DATE
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_ID
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_NOTE
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_TITLE
import com.fallen.spenwise.data.DatabaseHelper.Companion.COLUMN_USER_ID
import com.fallen.spenwise.data.DatabaseHelper.Companion.TABLE_EXPENSES
import com.fallen.spenwise.data.DatabaseHelper.Companion.TABLE_INCOME
import java.text.SimpleDateFormat
import java.util.*

class TransactionRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Add a new expense
    fun addExpense(uid: String, title: String, amount: Double, category: String, date: Date, note: String?): Long {
        val dateString = dateFormatter.format(date)
        val timestamp = System.currentTimeMillis()
        return dbHelper.addExpense(uid, title, amount, category, dateString, note, timestamp)
    }

    // Add a new income
    fun addIncome(uid: String, title: String, amount: Double, category: String, date: Date, note: String?): Long {
        val dateString = dateFormatter.format(date)
        val timestamp = System.currentTimeMillis()
        return dbHelper.addIncome(uid, title, amount, category, dateString, note, timestamp)
    }

    // Get all expenses for a user
    fun getExpenses(uid: String): List<Map<String, Any>> {
        return dbHelper.getExpenses(uid)
    }

    // Get all income for a user
    fun getIncome(uid: String): List<Map<String, Any>> {
        return dbHelper.getIncome(uid)
    }

    // Get all transactions (both income and expenses) for a user
    fun getAllTransactions(uid: String): List<Map<String, Any>> {
        return dbHelper.getAllTransactions(uid)
    }

    // Get user's total balance
    fun getUserTotal(uid: String): Double {
        val user = dbHelper.getUser(uid)
        return user?.get(DatabaseHelper.COLUMN_TOTAL) as? Double ?: 0.0
    }

    // Get total expenses for a user
    fun getTotalExpenses(uid: String): Double {
        val expenses = getExpenses(uid)
        return expenses.sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
    }

    // Get total income for a user
    fun getTotalIncome(uid: String): Double {
        val income = getIncome(uid)
        return income.sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
    }

    // Get expenses by category
    fun getExpensesByCategory(uid: String): Map<String, Double> {
        val expenses = getExpenses(uid)
        val categoryTotals = mutableMapOf<String, Double>()
        
        for (expense in expenses) {
            val category = expense[DatabaseHelper.COLUMN_CATEGORY] as String
            val amount = expense[DatabaseHelper.COLUMN_AMOUNT] as Double
            categoryTotals[category] = (categoryTotals[category] ?: 0.0) + amount
        }
        
        return categoryTotals
    }

    // Get income by category
    fun getIncomeByCategory(uid: String): Map<String, Double> {
        val income = getIncome(uid)
        val categoryTotals = mutableMapOf<String, Double>()
        
        for (inc in income) {
            val category = inc[DatabaseHelper.COLUMN_CATEGORY] as String
            val amount = inc[DatabaseHelper.COLUMN_AMOUNT] as Double
            categoryTotals[category] = (categoryTotals[category] ?: 0.0) + amount
        }
        
        return categoryTotals
    }

    // Get transactions for a specific date range
    fun getTransactionsByDateRange(uid: String, startDate: Date, endDate: Date): List<Map<String, Any>> {
        val startDateString = dateFormatter.format(startDate)
        val endDateString = dateFormatter.format(endDate)
        
        val allTransactions = getAllTransactions(uid)
        return allTransactions.filter { transaction ->
            val transactionDate = transaction[DatabaseHelper.COLUMN_DATE] as String
            transactionDate in startDateString..endDateString
        }
    }

    // Delete a transaction
    fun deleteTransaction(transactionId: Int, isExpense: Boolean): Boolean {
        return if (isExpense) {
            dbHelper.deleteExpense(transactionId)
        } else {
            dbHelper.deleteIncome(transactionId)
        }
    }

    fun getExpenseById(expenseId: Int): Map<String, Any>? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_EXPENSES,
            null,
            "$COLUMN_ID = ?",
            arrayOf(expenseId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val expense = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                COLUMN_TITLE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                COLUMN_AMOUNT to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                COLUMN_CATEGORY to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                COLUMN_DATE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                COLUMN_NOTE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE))
            )
            cursor.close()
            expense
        } else {
            cursor.close()
            null
        }
    }

    fun getIncomeById(incomeId: Int): Map<String, Any>? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_INCOME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(incomeId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val income = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                COLUMN_TITLE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                COLUMN_AMOUNT to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                COLUMN_CATEGORY to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                COLUMN_DATE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                COLUMN_NOTE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE))
            )
            cursor.close()
            income
        } else {
            cursor.close()
            null
        }
    }

    fun updateExpense(
        expenseId: Int,
        title: String,
        amount: Double,
        category: String,
        date: String,
        note: String
    ): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
            put(COLUMN_NOTE, note)
        }

        return try {
            val rowsAffected = db.update(
                TABLE_EXPENSES,
                values,
                "$COLUMN_ID = ?",
                arrayOf(expenseId.toString())
            )
            rowsAffected > 0
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error updating expense: ${e.message}")
            false
        }
    }

    fun updateIncome(
        incomeId: Int,
        title: String,
        amount: Double,
        category: String,
        date: String,
        note: String
    ): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
            put(COLUMN_NOTE, note)
        }

        return try {
            val rowsAffected = db.update(
                TABLE_INCOME,
                values,
                "$COLUMN_ID = ?",
                arrayOf(incomeId.toString())
            )
            rowsAffected > 0
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error updating income: ${e.message}")
            false
        }
    }
} 