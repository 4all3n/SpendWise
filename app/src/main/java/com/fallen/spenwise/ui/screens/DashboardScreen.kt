package com.fallen.spenwise.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min
import com.fallen.spenwise.ui.components.BottomNavigationBar
import com.fallen.spenwise.model.Transaction
import com.fallen.spenwise.model.TransactionType
import com.fallen.spenwise.data.TransactionRepository
import com.fallen.spenwise.data.UserRepository
import com.fallen.spenwise.data.DatabaseHelper
import androidx.compose.ui.platform.LocalContext

@Composable
fun DashboardScreen(
    onBudgetClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAddTransaction: () -> Unit = {},
    onNavigateToTransactions: () -> Unit = {}
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "User"
    var selectedTab by remember { mutableStateOf(0) }
    val lazyListState = rememberLazyListState()
    
    // Get repositories
    val context = LocalContext.current
    val transactionRepository = remember { TransactionRepository(context) }
    val userRepository = remember { UserRepository(context) }
    val currentUserId = remember { userRepository.getCurrentUserId() }
    
    // State for transactions and totals
    var transactions by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var totalIncome by remember { mutableStateOf(0.0) }
    var totalExpenses by remember { mutableStateOf(0.0) }
    var categoryTotals by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    
    // Load data when screen is created or when currentUserId changes
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            // Get transactions
            val expenses = transactionRepository.getExpenses(currentUserId)
            val income = transactionRepository.getIncome(currentUserId)
            
            // Combine and sort transactions
            val allTransactions = (expenses.map { it + ("isExpense" to true) } +
                                income.map { it + ("isExpense" to false) })
                .sortedByDescending { it[DatabaseHelper.COLUMN_ID] as Int }
            
            // Calculate totals
            totalIncome = income.sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
            totalExpenses = expenses.sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
            
            // Calculate category totals
            categoryTotals = expenses.groupBy { it[DatabaseHelper.COLUMN_CATEGORY] as String }
                .mapValues { (_, transactions) -> 
                    transactions.sumOf { it[DatabaseHelper.COLUMN_AMOUNT] as Double }
                }
            
            transactions = allTransactions
        }
    }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Calculate scroll progress for animations
    val firstVisibleItemScrollOffset by remember {
        derivedStateOf { lazyListState.firstVisibleItemScrollOffset }
    }
    val isScrolled by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .graphicsLayer {
                    this.alpha = alpha
                },
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Header with user info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back,",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = userName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF8D5CF5),
                                            Color(0xFFB06AB3)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Total Balance Card with gradient
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF8D5CF5),
                                            Color(0xFFB06AB3)
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Balance",
                                            fontSize = 16.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "₹${(totalIncome - totalExpenses).toInt()}",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_wallet),
                                            contentDescription = "Wallet",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Last 30 days",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_trending_up),
                                            contentDescription = "Trending up",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "+₹${totalIncome.toInt()}",
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Quick Actions
                    Text(
                        text = "Expense Categories",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF2A2F3C)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            categoryTotals.forEach { (category, amount) ->
                                ExpenseCategoryItem(
                                    icon = getCategoryIcon(category),
                                    text = category,
                                    percentage = "${((amount / totalExpenses) * 100).toInt()}%",
                                    color = getCategoryColor(category)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Enhanced Add Transaction Button Section
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable(onClick = onAddTransaction),
                        shape = RoundedCornerShape(28.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF8D5CF5),
                                            Color(0xFFB06AB3)
                                        )
                                    )
                                )
                                .padding(horizontal = 32.dp, vertical = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Glowing add icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = Color.White.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        )
                                        .padding(2.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.9f)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add Transaction",
                                                tint = Color(0xFF8D5CF5),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Add Transaction",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Income or Expense",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.95f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Recent Transactions with new design
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "See All",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8D5CF5),
                            modifier = Modifier.clickable { onNavigateToTransactions() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Recent Transactions List with spacing
            items(transactions.take(5)) { transaction ->
                TransactionItem(
                    title = transaction[DatabaseHelper.COLUMN_TITLE] as String,
                    amount = transaction[DatabaseHelper.COLUMN_AMOUNT] as Double,
                    category = transaction[DatabaseHelper.COLUMN_CATEGORY] as String,
                    date = transaction[DatabaseHelper.COLUMN_DATE] as String,
                    isExpense = transaction["isExpense"] as Boolean
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                selectedTab = 0,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        1 -> onNavigateToTransactions()
                        2 -> onBudgetClick()
                        3 -> onSettingsClick()
                    }
                }
            )
        }
    }
}

@Composable
private fun TransactionItem(
    title: String,
    amount: Double,
    category: String,
    date: String,
    isExpense: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF2A2F3C)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Modern icon design with rounded corners
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                getCategoryColor(category).copy(alpha = 0.2f),
                                getCategoryColor(category).copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getCategoryIcon(category)),
                    contentDescription = category,
                    tint = getCategoryColor(category),
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = category,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (isExpense) "-" else "+"}₹${amount.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isExpense) Color.White else Color(0xFF4CAF50)
                )
                Text(
                    text = date,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ExpenseCategoryItem(
    icon: Int,
    text: String,
    percentage: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = text,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = percentage,
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getCategoryIcon(category: String): Int {
    return when (category.lowercase()) {
        "food & dining" -> R.drawable.ic_food
        "travel" -> R.drawable.ic_travel
        "shopping" -> R.drawable.ic_shopping
        "entertainment" -> R.drawable.ic_entertainment
        "bills" -> R.drawable.ic_bills
        "salary" -> R.drawable.ic_wallet
        "freelance" -> R.drawable.ic_freelance
        "investment" -> R.drawable.ic_investment
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
        "salary" -> Color(0xFF4CAF50)
        "freelance" -> Color(0xFFFF9800)
        "investment" -> Color(0xFF9C27B0)
        "others" -> Color(0xFF6B7280)
        else -> Color(0xFF6B7280)
    }
} 