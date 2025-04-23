package com.fallen.spenwise.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.data.TransactionRepository
import com.fallen.spenwise.data.DatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transactionId: Int,
    isExpense: Boolean,
    onNavigateBack: () -> Unit,
    context: Context
) {
    val transactionRepository = remember { TransactionRepository(context) }
    val currentUser = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "current_user" }
    val scrollState = rememberScrollState()
    
    // State variables
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    
    // Load transaction details
    LaunchedEffect(transactionId) {
        val transaction = if (isExpense) {
            transactionRepository.getExpenseById(transactionId)
        } else {
            transactionRepository.getIncomeById(transactionId)
        }
        
        transaction?.let {
            title = it[DatabaseHelper.COLUMN_TITLE] as String
            amount = (it[DatabaseHelper.COLUMN_AMOUNT] as Double).toInt().toString()
            category = it[DatabaseHelper.COLUMN_CATEGORY] as String
            note = it[DatabaseHelper.COLUMN_NOTE] as? String ?: ""
            date = it[DatabaseHelper.COLUMN_DATE] as String
        }
    }

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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Edit Transaction",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Transaction Type Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF282C35),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isExpense) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = if (isExpense) R.drawable.ic_expense else R.drawable.ic_income),
                            contentDescription = null,
                            tint = if (isExpense) Color(0xFFEF4444) else Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isExpense) "Expense" else "Income",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Current Values Section
            Text(
                text = "Current Values",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF282C35),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Amount (now at the top for emphasis)
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Amount",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₹${(amount.toDoubleOrNull() ?: 0.0).toInt()}",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title and Category in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Title
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Title",
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Category
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Category",
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = category,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Date
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Date",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = date,
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!note.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Note",
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = note,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Section
            Text(
                text = "Edit Values",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { 
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        amount = it
                    }
                },
                label = { Text("Amount", color = Color.White.copy(alpha = 0.7f)) },
                prefix = { Text("₹", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Input
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = true },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White.copy(alpha = 0.7f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { isExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select category",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                )

                // Category Dropdown Menu
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier
                        .background(
                            color = Color(0xFF282C35),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    val categories = if (isExpense) {
                        listOf(
                            "Food & Dining" to R.drawable.ic_food,
                            "Shopping" to R.drawable.ic_shopping,
                            "Bills" to R.drawable.ic_bills,
                            "Entertainment" to R.drawable.ic_entertainment,
                            "Travel" to R.drawable.ic_travel,
                            "Others" to R.drawable.ic_others
                        )
                    } else {
                        listOf(
                            "Salary" to R.drawable.ic_wallet,
                            "Freelance" to R.drawable.ic_freelance,
                            "Investment" to R.drawable.ic_investment,
                            "Others" to R.drawable.ic_dollar
                        )
                    }

                    categories.forEach { (cat, icon) ->
                        DropdownMenuItem(
                            onClick = {
                                category = cat
                                isExpanded = false
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isExpense) 
                                                    Color(0xFFEF4444).copy(alpha = 0.2f) 
                                                else 
                                                    Color(0xFF10B981).copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = icon),
                                            contentDescription = null,
                                            tint = if (isExpense) 
                                                Color(0xFFEF4444) 
                                            else 
                                                Color(0xFF10B981),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        text = cat,
                                        color = Color.White
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    val newAmount = amount.toIntOrNull()
                    if (newAmount != null && newAmount > 0 && title.isNotEmpty() && category.isNotEmpty()) {
                        if (isExpense) {
                            if (transactionRepository.updateExpense(
                                    transactionId,
                                    title,
                                    newAmount.toDouble(),
                                    category,
                                    date,
                                    note
                                )) {
                                Toast.makeText(context, "Expense updated successfully", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Failed to update expense", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (transactionRepository.updateIncome(
                                    transactionId,
                                    title,
                                    newAmount.toDouble(),
                                    category,
                                    date,
                                    note
                                )) {
                                Toast.makeText(context, "Income updated successfully", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Failed to update income", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Button
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Delete Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val success = transactionRepository.deleteTransaction(transactionId, isExpense)
                        if (success) {
                            Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Failed to delete transaction", Toast.LENGTH_SHORT).show()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun getIconForCategory(category: String): Int {
    return when (category.lowercase()) {
        "food & dining" -> R.drawable.ic_food
        "shopping" -> R.drawable.ic_shopping
        "bills" -> R.drawable.ic_bills
        "entertainment" -> R.drawable.ic_entertainment
        "travel" -> R.drawable.ic_travel
        "salary" -> R.drawable.ic_wallet
        "freelance" -> R.drawable.ic_freelance
        "investment" -> R.drawable.ic_investment
        else -> R.drawable.ic_others
    }
} 