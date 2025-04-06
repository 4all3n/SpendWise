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

// Data class for Transaction
data class Transaction(
    val type: TransactionType,
    val color: Color,
    val title: String,
    val subtitle: String,
    val amount: String,
    val date: String
)

@Composable
fun DashboardScreen() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "User"
    val lazyListState = rememberLazyListState()
    val transactions = remember { getSampleTransactions() }

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
                                            text = "$2,548.00",
                                            fontSize = 40.sp,
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
                                            text = "+12.5%",
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
                            ExpenseCategoryItem(
                                icon = R.drawable.ic_shopping,
                                text = "Shopping",
                                percentage = "35%",
                                color = Color(0xFF8D5CF5)
                            )
                            ExpenseCategoryItem(
                                icon = R.drawable.ic_food,
                                text = "Food",
                                percentage = "25%",
                                color = Color(0xFFE96D71)
                            )
                            ExpenseCategoryItem(
                                icon = R.drawable.ic_bills,
                                text = "Bills",
                                percentage = "20%",
                                color = Color(0xFF4C9EFF)
                            )
                            ExpenseCategoryItem(
                                icon = R.drawable.ic_others,
                                text = "Others",
                                percentage = "20%",
                                color = Color(0xFF4CAF50)
                            )
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { /* TODO: Navigate to transactions */ },
                            color = Color(0xFF8D5CF5).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "See All",
                                fontSize = 14.sp,
                                color = Color(0xFF8D5CF5),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Recent Transactions List with spacing
            items(transactions) { transaction ->
                TransactionItem(transaction = transaction)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Bottom Navigation with blur effect
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(80.dp),
            color = Color(0xFF2A2F3C),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(
                        icon = R.drawable.ic_home,
                        isSelected = true
                    )
                    NavItem(
                        icon = R.drawable.ic_stats
                    )
                    NavItem(
                        icon = R.drawable.ic_wallet
                    )
                    NavItem(
                        icon = R.drawable.ic_settings
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: Int,
    text: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO: Handle quick action */ }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NavItem(
    icon: Int,
    isSelected: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8D5CF5),
                            Color(0xFFB06AB3)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                }
            )
            .clickable { /* TODO: Handle navigation */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
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
                                transaction.color.copy(alpha = 0.2f),
                                transaction.color.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (transaction.type) {
                    TransactionType.FOOD -> Icon(
                        painter = painterResource(id = R.drawable.ic_food),
                        contentDescription = "Food",
                        tint = transaction.color,
                        modifier = Modifier.size(22.dp)
                    )
                    TransactionType.SHOPPING -> Icon(
                        painter = painterResource(id = R.drawable.ic_shopping),
                        contentDescription = "Shopping",
                        tint = transaction.color,
                        modifier = Modifier.size(22.dp)
                    )
                    TransactionType.TRANSPORT -> Icon(
                        painter = painterResource(id = R.drawable.ic_others),
                        contentDescription = "Transport",
                        tint = transaction.color,
                        modifier = Modifier.size(22.dp)
                    )
                    TransactionType.ENTERTAINMENT -> Icon(
                        painter = painterResource(id = R.drawable.ic_others),
                        contentDescription = "Entertainment",
                        tint = transaction.color,
                        modifier = Modifier.size(22.dp)
                    )
                    TransactionType.INCOME -> Icon(
                        painter = painterResource(id = R.drawable.ic_wallet),
                        contentDescription = "Income",
                        tint = transaction.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = transaction.subtitle,
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
                    text = transaction.amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (transaction.amount.startsWith("+")) Color(0xFF4CAF50) else Color.White
                )
                Text(
                    text = transaction.date,
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

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

private fun getSampleTransactions(): List<Transaction> {
    return listOf(
        Transaction(
            type = TransactionType.FOOD,
            color = Color(0xFF8D5CF5),
            title = "Restaurant",
            subtitle = "Lunch with colleagues",
            amount = "-$45.50",
            date = "Today"
        ),
        Transaction(
            type = TransactionType.SHOPPING,
            color = Color(0xFFB06AB3),
            title = "Shopping",
            subtitle = "Grocery store",
            amount = "-$125.30",
            date = "Yesterday"
        ),
        Transaction(
            type = TransactionType.TRANSPORT,
            color = Color(0xFFE96D71),
            title = "Transport",
            subtitle = "Uber ride",
            amount = "-$22.15",
            date = "Yesterday"
        ),
        Transaction(
            type = TransactionType.ENTERTAINMENT,
            color = Color(0xFF8D5CF5),
            title = "Entertainment",
            subtitle = "Cinema tickets",
            amount = "-$35.00",
            date = "23 Mar"
        ),
        Transaction(
            type = TransactionType.INCOME,
            color = Color(0xFF4CAF50),
            title = "Salary",
            subtitle = "Monthly payment",
            amount = "+$4,500.00",
            date = "22 Mar"
        )
    )
}

// Add TransactionType enum
enum class TransactionType {
    FOOD,
    SHOPPING,
    TRANSPORT,
    ENTERTAINMENT,
    INCOME
} 