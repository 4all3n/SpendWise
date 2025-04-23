package com.fallen.spenwise.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import com.fallen.spenwise.model.BudgetCategory
import com.fallen.spenwise.data.BudgetRepository
import com.fallen.spenwise.data.DatabaseHelper
import com.fallen.spenwise.ui.components.BottomNavigationBar
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddBudget: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToEditBudget: (String, Double) -> Unit,
    context: android.content.Context
) {
    var selectedMonth by remember { mutableStateOf(getCurrentMonth()) }
    var selectedTab by remember { mutableStateOf(2) }
    
    // Get data from database
    val budgetRepository = remember { BudgetRepository(context) }
    val currentUser = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "current_user" }
    var budgets by remember { mutableStateOf(budgetRepository.getActiveBudgets(currentUser)) }
    
    // Calculate total budget and spent amounts
    val totalBudget = remember(budgets) {
        budgets.sumOf { it[DatabaseHelper.COLUMN_LIMIT] as Double }
    }
    
    val totalSpent = remember(budgets) {
        budgets.sumOf { budget ->
            val category = budget[DatabaseHelper.COLUMN_CATEGORY] as String
            budgetRepository.getBudgetUsage(currentUser, category)
        }
    }
    
    val remainingBudget = totalBudget - totalSpent
    val isOverBudget = totalSpent > totalBudget
    
    // Convert budgets to BudgetCategory objects for display
    val categories = remember(budgets) {
        budgets.map { budget ->
            val categoryName = budget[DatabaseHelper.COLUMN_CATEGORY] as String
            val limit = budget[DatabaseHelper.COLUMN_LIMIT] as Double
            val spent = budgetRepository.getBudgetUsage(currentUser, categoryName)
            
            BudgetCategory(
                name = categoryName,
                icon = getCategoryIcon(categoryName),
                spent = spent,
                budget = limit,
                color = getCategoryColor(categoryName)
            )
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
                .padding(bottom = 80.dp)
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
        ) {
                // Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Budget",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Track your spending limits",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Month Selector Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color(0xFF282C35),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { /* Previous month */ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_left),
                                    contentDescription = "Previous month",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Text(
                                text = selectedMonth,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            
                            IconButton(
                                onClick = { /* Next month */ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_right),
                                    contentDescription = "Next month",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Total Budget Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF282C35),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Total Budget",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                            text = "₹${totalBudget.toInt()}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            LinearProgressIndicator(
                            progress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isOverBudget) Color(0xFFEF4444) else Color(0xFF8B5CF6),
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                text = "Spent: ₹${totalSpent.toInt()}",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                text = "Remaining: ₹${remainingBudget.toInt()}",
                                    fontSize = 14.sp,
                                    color = if (isOverBudget) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    // Add Category Button
                    Button(
                    onClick = { onNavigateToAddBudget() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                        contentDescription = "Add Budget",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                        text = "Add Budget",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                    )
                }
            }

            // Categories List
            Column {
                categories.forEach { category ->
                    CategoryCard(
                        category = category,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        onDelete = {
                            val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: "current_user"
                            val budgetRepository = BudgetRepository(context)
                            if (budgetRepository.deleteBudget(currentUser, category.name)) {
                                Toast.makeText(context, "Budget deleted successfully", Toast.LENGTH_SHORT).show()
                                // Refresh the budgets list
                                budgets = budgetRepository.getActiveBudgets(currentUser)
                            } else {
                                Toast.makeText(context, "Failed to delete budget", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onEdit = {
                            onNavigateToEditBudget(category.name, category.budget)
                        }
                    )
                }
            }

            // Bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { newTab ->
                    if (newTab != selectedTab) {
                        selectedTab = newTab
                        when (newTab) {
                            0 -> onNavigateBack() // Navigate back to Dashboard
                            1 -> onNavigateToTransactions()
                            3 -> onSettingsClick()
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryCard(
    category: BudgetCategory,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isOverBudget = category.spent > category.budget
    
    Surface(
        modifier = modifier
            .combinedClickable(
                onClick = { onEdit() },
                onLongClick = { showDeleteDialog = true }
            ),
        color = Color(0xFF282C35),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(category.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = category.icon),
                            contentDescription = null,
                            tint = category.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Category Details
                    Column {
                        Text(
                            text = category.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "${(category.spent / category.budget * 100).toInt()}% of budget",
                            fontSize = 14.sp,
                            color = if (isOverBudget) Color(0xFFEF4444) else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Amount
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹${category.spent.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "of ₹${category.budget.toInt()}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { (category.spent / category.budget).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isOverBudget) Color(0xFFEF4444) else category.color,
                trackColor = Color.White.copy(alpha = 0.1f),
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Budget", color = Color.White) },
            text = { Text("Are you sure you want to delete this budget?", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF282C35),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

// Helper functions
private fun getCurrentMonth(): String {
    val calendar = Calendar.getInstance()
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return monthFormat.format(calendar.time)
}

private fun getCategoryIcon(category: String): Int {
    return when (category.lowercase()) {
        "food & dining" -> R.drawable.ic_food
        "travel" -> R.drawable.ic_travel
        "shopping" -> R.drawable.ic_shopping
        "entertainment" -> R.drawable.ic_entertainment
        "bills" -> R.drawable.ic_bills
        "others" -> R.drawable.ic_others
        else -> R.drawable.ic_others
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food & dining" -> Color(0xFF8B5CF6)
        "travel" -> Color(0xFF10B981)
        "shopping" -> Color(0xFFEF4444)
        "entertainment" -> Color(0xFFF59E0B)
        "bills" -> Color(0xFF3B82F6)
        "others" -> Color(0xFF6B7280)
        else -> Color(0xFF6B7280)
    }
}

data class BudgetCategory(
    val name: String,
    val icon: Int,
    val spent: Double,
    val budget: Double,
    val color: Color
) 