package com.fallen.spenwise.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class TransactionRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Add a new expense
    fun addExpense(uid: String, title: String, amount: Double, category: String, date: Date, note: String?): Long {
        val dateString = dateFormatter.format(date)
        return dbHelper.addExpense(uid, title, amount, category, dateString, note)
    }

    // Add a new income
    fun addIncome(uid: String, title: String, amount: Double, category: String, date: Date, note: String?): Long {
        val dateString = dateFormatter.format(date)
        return dbHelper.addIncome(uid, title, amount, category, dateString, note)
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
} 