package com.fallen.spenwise.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.ui.components.BottomNavigationBar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.platform.LocalContext
import com.fallen.spenwise.data.DatabaseHelper
import com.fallen.spenwise.data.TransactionRepository
import com.fallen.spenwise.data.UserRepository
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAddTransaction: () -> Unit = {},
    onNavigateToEditTransaction: (Int, Boolean) -> Unit = { _, _ -> }
) {
    var selectedTab by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val transactionRepository = remember { TransactionRepository(context) }
    val userRepository = remember { UserRepository(context) }
    val currentUserId = remember { userRepository.getCurrentUserId() }
    
    // State for transactions
    var transactions by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    
    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Map<String, Any>?>(null) }
    
    // Load transactions when screen is created or when currentUserId changes
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            val expenses = transactionRepository.getExpenses(currentUserId)
            val income = transactionRepository.getIncome(currentUserId)
            
            // Combine transactions and add isExpense flag
            val allTransactions = (expenses.map { it + ("isExpense" to true) } +
                                income.map { it + ("isExpense" to false) })
            
            // Sort transactions by ID in descending order (newest first)
            transactions = allTransactions.sortedByDescending { 
                it[DatabaseHelper.COLUMN_ID] as Int 
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                transactionToDelete = null
            },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Delete the transaction
                        transactionRepository.deleteTransaction(
                            transactionToDelete!![DatabaseHelper.COLUMN_ID] as Int,
                            transactionToDelete!!["isExpense"] as Boolean
                        )
                        // Refresh transactions
                        if (currentUserId != null) {
                            val expenses = transactionRepository.getExpenses(currentUserId)
                            val income = transactionRepository.getIncome(currentUserId)
                            transactions = (expenses.map { it + ("isExpense" to true) } +
                                         income.map { it + ("isExpense" to false) })
                                    .sortedByDescending { it[DatabaseHelper.COLUMN_ID] as Int }
                        }
                        showDeleteDialog = false
                        transactionToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFE57373))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false
                        transactionToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { newTab ->
                    selectedTab = newTab
                    when (newTab) {
                        0 -> onNavigateToHome()
                        2 -> onNavigateToStats()
                        3 -> onNavigateToSettings()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = Color(0xFF8D5CF5),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B1E27),
                            Color(0xFF232731)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Background decorative elements
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = (-100).dp, y = (-100).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8D5CF5).copy(alpha = 0.1f),
                                Color(0xFF8D5CF5).copy(alpha = 0.0f)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 200.dp, y = 400.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFB06AB3).copy(alpha = 0.1f),
                                Color(0xFFB06AB3).copy(alpha = 0.0f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            ) {
                // Header
                Text(
                    text = "Transactions",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (transactions.isEmpty()) {
                    // Show empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions yet",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    // Group transactions by date
                    val groupedTransactions = transactions.groupBy { 
                        it[DatabaseHelper.COLUMN_DATE] as String 
                    }

                    // Transactions List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        groupedTransactions.forEach { (date, dateTransactions) ->
                            item {
                                Text(
                                    text = date,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(dateTransactions) { transaction ->
                                TransactionItem(
                                    title = transaction[DatabaseHelper.COLUMN_TITLE] as String,
                                    amount = transaction[DatabaseHelper.COLUMN_AMOUNT] as Double,
                                    category = transaction[DatabaseHelper.COLUMN_CATEGORY] as String,
                                    date = transaction[DatabaseHelper.COLUMN_DATE] as String,
                                    note = transaction[DatabaseHelper.COLUMN_NOTE] as? String,
                                    isExpense = transaction["isExpense"] as Boolean,
                                    transactionId = transaction[DatabaseHelper.COLUMN_ID] as Int,
                                    onDelete = {
                                        transactionToDelete = transaction
                                        showDeleteDialog = true
                                    },
                                    onEdit = {
                                        onNavigateToEditTransaction(
                                            transaction[DatabaseHelper.COLUMN_ID] as Int,
                                            transaction["isExpense"] as Boolean
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TransactionItem(
    title: String,
    amount: Double,
    category: String,
    date: String,
    note: String?,
    isExpense: Boolean,
    transactionId: Int,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onEdit,
                onLongClick = onDelete
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF282C35),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(getColorForCategory(category).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = getIconForCategory(category)),
                        contentDescription = category,
                        tint = getColorForCategory(category),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Transaction Details
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = category,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    if (!note.isNullOrEmpty()) {
                        Text(
                            text = note,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (isExpense) "-" else "+"}₹${amount.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) Color(0xFFEF4444) else Color(0xFF10B981)
                )
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun getColorForCategory(category: String): Color {
    return when (category) {
        "Food & Dining" -> Color(0xFF8D5CF5)  // Purple
        "Shopping" -> Color(0xFFB06AB3)       // Pink
        "Bills" -> Color(0xFFE96D71)          // Red
        "Entertainment" -> Color(0xFF4CAF50)  // Green
        "Travel" -> Color(0xFF2196F3)         // Blue
        "Salary" -> Color(0xFF4CAF50)         // Green
        "Freelance" -> Color(0xFFFF9800)      // Orange
        "Investment" -> Color(0xFF9C27B0)     // Deep Purple
        else -> Color(0xFF607D8B)             // Blue Grey
    }
}

private fun getIconForCategory(category: String): Int {
    return when (category) {
        "Food & Dining" -> R.drawable.ic_food
        "Shopping" -> R.drawable.ic_shopping
        "Bills" -> R.drawable.ic_bills
        "Entertainment" -> R.drawable.ic_entertainment
        "Travel" -> R.drawable.ic_travel
        "Salary" -> R.drawable.ic_wallet
        "Freelance" -> R.drawable.ic_freelance
        "Investment" -> R.drawable.ic_investment
        else -> R.drawable.ic_others
    }
} 