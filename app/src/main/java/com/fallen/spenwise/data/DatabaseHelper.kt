package com.fallen.spenwise.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class DatabaseHelper(private val appContext: Context) : SQLiteOpenHelper(appContext, DATABASE_NAME, null, DATABASE_VERSION) {

    // Add a public getter for the context
    val context: Context
        get() = appContext

    companion object {
        private const val DATABASE_NAME = "SpendWiseDB"
        private const val DATABASE_VERSION = 2

        // Table names
        const val TABLE_USERS = "users"
        const val TABLE_EXPENSES = "expenses"
        const val TABLE_INCOME = "income"
        const val TABLE_BUDGET = "budget"

        // Common column names
        const val COLUMN_ID = "id"
        const val COLUMN_UID = "uid"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DATE = "date"
        const val COLUMN_NOTE = "note"
        const val COLUMN_TIMESTAMP = "timestamp"

        // Users table column names
        const val COLUMN_NAME = "name"
        const val COLUMN_TOTAL = "total"

        // Budget table column names
        const val COLUMN_LIMIT = "limit_amount"
        const val COLUMN_START_DATE = "start_date"
        const val COLUMN_END_DATE = "end_date"

        // New column for user ID
        const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create users table
        val createUsersTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_UID TEXT UNIQUE NOT NULL, " +
                "$COLUMN_NAME TEXT NOT NULL, " +
                "$COLUMN_TOTAL REAL DEFAULT 0.0" +
                ")"

        // Create expenses table
        val createExpensesTable = "CREATE TABLE $TABLE_EXPENSES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_UID TEXT NOT NULL, " +
                "$COLUMN_TITLE TEXT NOT NULL, " +
                "$COLUMN_AMOUNT INTEGER NOT NULL, " +
                "$COLUMN_CATEGORY TEXT NOT NULL, " +
                "$COLUMN_DATE TEXT NOT NULL, " +
                "$COLUMN_NOTE TEXT, " +
                "$COLUMN_TIMESTAMP INTEGER NOT NULL, " +
                "FOREIGN KEY($COLUMN_UID) REFERENCES $TABLE_USERS($COLUMN_UID) ON DELETE CASCADE" +
                ")"

        // Create income table
        val createIncomeTable = "CREATE TABLE $TABLE_INCOME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_UID TEXT NOT NULL, " +
                "$COLUMN_TITLE TEXT NOT NULL, " +
                "$COLUMN_AMOUNT INTEGER NOT NULL, " +
                "$COLUMN_CATEGORY TEXT NOT NULL, " +
                "$COLUMN_DATE TEXT NOT NULL, " +
                "$COLUMN_NOTE TEXT, " +
                "$COLUMN_TIMESTAMP INTEGER NOT NULL, " +
                "FOREIGN KEY($COLUMN_UID) REFERENCES $TABLE_USERS($COLUMN_UID) ON DELETE CASCADE" +
                ")"

        // Create budget table
        val createBudgetTable = "CREATE TABLE $TABLE_BUDGET (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_UID TEXT NOT NULL, " +
                "$COLUMN_CATEGORY TEXT NOT NULL, " +
                "$COLUMN_LIMIT INTEGER NOT NULL, " +
                "$COLUMN_START_DATE TEXT NOT NULL, " +
                "$COLUMN_END_DATE TEXT NOT NULL, " +
                "FOREIGN KEY($COLUMN_UID) REFERENCES $TABLE_USERS($COLUMN_UID) ON DELETE CASCADE" +
                ")"

        db.execSQL(createUsersTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createIncomeTable)
        db.execSQL(createBudgetTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add timestamp column to expenses table
            db.execSQL("ALTER TABLE $TABLE_EXPENSES ADD COLUMN $COLUMN_TIMESTAMP INTEGER NOT NULL DEFAULT 0")
            // Add timestamp column to income table
            db.execSQL("ALTER TABLE $TABLE_INCOME ADD COLUMN $COLUMN_TIMESTAMP INTEGER NOT NULL DEFAULT 0")
        }
    }

    // User operations
    fun addUser(uid: String, name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_UID, uid)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_TOTAL, 0.0)
        return db.insert(TABLE_USERS, null, values)
    }

    fun getUser(uid: String): Map<String, Any>? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_UID = ?",
            arrayOf(uid),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val user = HashMap<String, Any>()
            user[COLUMN_UID] = cursor.getString(cursor.getColumnIndex(COLUMN_UID))
            user[COLUMN_NAME] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            user[COLUMN_TOTAL] = cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL))
            cursor.close()
            return user
        }
        cursor.close()
        return null
    }

    fun updateUserTotal(uid: String, newTotal: Double) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TOTAL, newTotal)
        db.update(TABLE_USERS, values, "$COLUMN_UID = ?", arrayOf(uid))
    }

    // Expense operations
    fun addExpense(uid: String, title: String, amount: Double, category: String, date: String, note: String?, timestamp: Long): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_UID, uid)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_AMOUNT, amount.toInt())
        values.put(COLUMN_CATEGORY, category)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_NOTE, note)
        values.put(COLUMN_TIMESTAMP, timestamp)
        
        // Update user's total (decrease by expense amount)
        val user = getUser(uid)
        if (user != null) {
            val currentTotal = user[COLUMN_TOTAL] as Double
            updateUserTotal(uid, currentTotal - amount)
        }
        
        return db.insert(TABLE_EXPENSES, null, values)
    }

    fun getExpenses(uid: String): List<Map<String, Any>> {
        val db = this.readableDatabase
        val expenses = ArrayList<Map<String, Any>>()
        
        val cursor = db.query(
            TABLE_EXPENSES,
            null,
            "$COLUMN_UID = ?",
            arrayOf(uid),
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"  // Sort by timestamp in descending order
        )

        if (cursor.moveToFirst()) {
            do {
                val expense = HashMap<String, Any>()
                expense[COLUMN_ID] = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                expense[COLUMN_TITLE] = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                expense[COLUMN_AMOUNT] = cursor.getInt(cursor.getColumnIndex(COLUMN_AMOUNT)).toDouble()
                expense[COLUMN_CATEGORY] = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                expense[COLUMN_DATE] = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                expense[COLUMN_NOTE] = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                expense[COLUMN_TIMESTAMP] = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                expenses.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return expenses
    }

    // Income operations
    fun addIncome(uid: String, title: String, amount: Double, category: String, date: String, note: String?, timestamp: Long): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_UID, uid)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_AMOUNT, amount.toInt())
        values.put(COLUMN_CATEGORY, category)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_NOTE, note)
        values.put(COLUMN_TIMESTAMP, timestamp)
        
        // Update user's total (increase by income amount)
        val user = getUser(uid)
        if (user != null) {
            val currentTotal = user[COLUMN_TOTAL] as Double
            updateUserTotal(uid, currentTotal + amount)
        }
        
        return db.insert(TABLE_INCOME, null, values)
    }

    fun getIncome(uid: String): List<Map<String, Any>> {
        val db = this.readableDatabase
        val incomeList = ArrayList<Map<String, Any>>()
        
        val cursor = db.query(
            TABLE_INCOME,
            null,
            "$COLUMN_UID = ?",
            arrayOf(uid),
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"  // Sort by timestamp in descending order
        )

        if (cursor.moveToFirst()) {
            do {
                val income = HashMap<String, Any>()
                income[COLUMN_ID] = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                income[COLUMN_TITLE] = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                income[COLUMN_AMOUNT] = cursor.getInt(cursor.getColumnIndex(COLUMN_AMOUNT)).toDouble()
                income[COLUMN_CATEGORY] = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                income[COLUMN_DATE] = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                income[COLUMN_NOTE] = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                income[COLUMN_TIMESTAMP] = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                incomeList.add(income)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return incomeList
    }

    // Delete expense by ID
    fun deleteExpense(expenseId: Int): Boolean {
        val db = this.writableDatabase
        // First get the expense details to update user's total
        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COLUMN_UID, COLUMN_AMOUNT),
            "$COLUMN_ID = ?",
            arrayOf(expenseId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val uid = cursor.getString(cursor.getColumnIndex(COLUMN_UID))
            val amount = cursor.getInt(cursor.getColumnIndex(COLUMN_AMOUNT)).toDouble()
            
            // Update user's total (increase by expense amount since we're deleting it)
            val user = getUser(uid)
            if (user != null) {
                val currentTotal = user[COLUMN_TOTAL] as Double
                updateUserTotal(uid, currentTotal + amount)
            }
            
            cursor.close()
            
            // Delete the expense
            val result = db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(expenseId.toString()))
            return result > 0
        }
        cursor.close()
        return false
    }

    // Delete income by ID
    fun deleteIncome(incomeId: Int): Boolean {
        val db = this.writableDatabase
        // First get the income details to update user's total
        val cursor = db.query(
            TABLE_INCOME,
            arrayOf(COLUMN_UID, COLUMN_AMOUNT),
            "$COLUMN_ID = ?",
            arrayOf(incomeId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val uid = cursor.getString(cursor.getColumnIndex(COLUMN_UID))
            val amount = cursor.getInt(cursor.getColumnIndex(COLUMN_AMOUNT)).toDouble()
            
            // Update user's total (decrease by income amount since we're deleting it)
            val user = getUser(uid)
            if (user != null) {
                val currentTotal = user[COLUMN_TOTAL] as Double
                updateUserTotal(uid, currentTotal - amount)
            }
            
            cursor.close()
            
            // Delete the income
            val result = db.delete(TABLE_INCOME, "$COLUMN_ID = ?", arrayOf(incomeId.toString()))
            return result > 0
        }
        cursor.close()
        return false
    }

    // Budget operations
    fun addBudget(uid: String, category: String, limit: Double, startDate: String, endDate: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_UID, uid)
        values.put(COLUMN_CATEGORY, category)
        values.put(COLUMN_LIMIT, limit.toInt())
        values.put(COLUMN_START_DATE, startDate)
        values.put(COLUMN_END_DATE, endDate)
        return db.insert(TABLE_BUDGET, null, values)
    }

    fun getBudgets(uid: String): List<Map<String, Any>> {
        val db = this.readableDatabase
        val budgets = ArrayList<Map<String, Any>>()
        
        val cursor = db.query(
            TABLE_BUDGET,
            null,
            "$COLUMN_UID = ?",
            arrayOf(uid),
            null,
            null,
            "$COLUMN_START_DATE DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val budget = HashMap<String, Any>()
                budget[COLUMN_ID] = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                budget[COLUMN_CATEGORY] = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                budget[COLUMN_LIMIT] = cursor.getInt(cursor.getColumnIndex(COLUMN_LIMIT)).toDouble()
                budget[COLUMN_START_DATE] = cursor.getString(cursor.getColumnIndex(COLUMN_START_DATE))
                budget[COLUMN_END_DATE] = cursor.getString(cursor.getColumnIndex(COLUMN_END_DATE))
                budgets.add(budget)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return budgets
    }

    // Delete a budget by category
    fun deleteBudget(uid: String, category: String): Boolean {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_UID = ? AND $COLUMN_CATEGORY = ?"
        val whereArgs = arrayOf(uid, category)
        val result = db.delete(TABLE_BUDGET, whereClause, whereArgs)
        return result > 0
    }

    // Get all transactions (both income and expenses) for a user
    fun getAllTransactions(uid: String): List<Map<String, Any>> {
        val transactions = ArrayList<Map<String, Any>>()
        
        // Get expenses
        val expenses = getExpenses(uid)
        for (expense in expenses) {
            val transaction = HashMap<String, Any>()
            transaction.putAll(expense)
            transaction["type"] = "expense"
            transactions.add(transaction)
        }
        
        // Get income
        val income = getIncome(uid)
        for (inc in income) {
            val transaction = HashMap<String, Any>()
            transaction.putAll(inc)
            transaction["type"] = "income"
            transactions.add(transaction)
        }
        
        // Sort by date (newest first)
        return transactions.sortedByDescending { it[COLUMN_DATE] as String }
    }

    // Delete all transactions and budgets for a user
    fun deleteAllUserData(uid: String): Boolean {
        val db = this.writableDatabase
        try {
            // Delete all expenses
            db.delete(TABLE_EXPENSES, "$COLUMN_UID = ?", arrayOf(uid))
            
            // Delete all income
            db.delete(TABLE_INCOME, "$COLUMN_UID = ?", arrayOf(uid))
            
            // Delete all budgets
            db.delete(TABLE_BUDGET, "$COLUMN_UID = ?", arrayOf(uid))
            
            // Reset user's total to 0
            val values = ContentValues()
            values.put(COLUMN_TOTAL, 0.0)
            db.update(TABLE_USERS, values, "$COLUMN_UID = ?", arrayOf(uid))
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
} 