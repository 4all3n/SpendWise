package com.fallen.spenwise.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.ui.components.BottomNavigationBar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.fallen.spenwise.model.Transaction
import com.fallen.spenwise.model.TransactionType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.clickable

@Composable
fun TransactionScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(1) }
    val transactions = remember { getSampleTransactions() }
    val groupedTransactions = transactions.groupBy { it.date }

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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1F2433))
                .padding(paddingValues)
        ) {
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

                // Transactions List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedTransactions.forEach { (date, transactions) ->
                        item {
                            Text(
                                text = date,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(transactions) { transaction ->
                            TransactionItem(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2A2F3C)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(transaction.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = getIconForType(transaction.type)),
                        contentDescription = transaction.type.name,
                        tint = transaction.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = transaction.subtitle,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
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

private fun getIconForType(type: TransactionType): Int {
    return when (type) {
        TransactionType.FOOD -> R.drawable.ic_food
        TransactionType.SHOPPING -> R.drawable.ic_shopping
        TransactionType.TRANSPORT -> R.drawable.ic_others
        TransactionType.ENTERTAINMENT -> R.drawable.ic_others
        TransactionType.INCOME -> R.drawable.ic_wallet
        TransactionType.EXPENSE -> R.drawable.ic_others
        TransactionType.BILLS -> R.drawable.ic_bills
        TransactionType.OTHERS -> R.drawable.ic_others
    }
}

private fun getSampleTransactions(): List<Transaction> {
    return listOf(
        Transaction(
            type = TransactionType.FOOD,
            color = Color(0xFF8D5CF5),
            title = "Restaurant",
            subtitle = "Lunch with colleagues",
            amount = "-₹45.50",
            date = "Today"
        ),
        Transaction(
            type = TransactionType.SHOPPING,
            color = Color(0xFFB06AB3),
            title = "Shopping",
            subtitle = "Grocery store",
            amount = "-₹125.30",
            date = "Yesterday"
        ),
        Transaction(
            type = TransactionType.TRANSPORT,
            color = Color(0xFFE96D71),
            title = "Transport",
            subtitle = "Uber ride",
            amount = "-₹22.15",
            date = "Yesterday"
        ),
        Transaction(
            type = TransactionType.ENTERTAINMENT,
            color = Color(0xFF8D5CF5),
            title = "Entertainment",
            subtitle = "Cinema tickets",
            amount = "-₹35.00",
            date = "Last Week"
        ),
        Transaction(
            type = TransactionType.INCOME,
            color = Color(0xFF4CAF50),
            title = "Salary",
            subtitle = "Monthly payment",
            amount = "+₹4,500.00",
            date = "Last Week"
        )
    )
} 