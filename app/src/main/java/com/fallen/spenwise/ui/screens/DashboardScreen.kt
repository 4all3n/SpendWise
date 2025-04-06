package com.fallen.spenwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1E27))
            .padding(24.dp)
    ) {
        // Top Bar with User Info and Notification
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2F3C))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Welcome Text
                Column {
                    Text(
                        text = "Hi, ${userName.split(" ")[0]}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Welcome back",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Notification Icon
            IconButton(
                onClick = { /* TODO: Handle notification click */ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF8D5CF5)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Total Balance",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale.US)
                            .format(8459.32),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_trending_up),
                            contentDescription = "Trending Up",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+2.3%",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
                
                Text(
                    text = "vs last month",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Expense Categories
        Text(
            text = "Expense Categories",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Percentages
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryItem(
                icon = R.drawable.ic_shopping,
                color = Color(0xFF8D5CF5),
                category = "Shopping",
                percentage = "35%"
            )
            CategoryItem(
                icon = R.drawable.ic_food,
                color = Color(0xFFF87171),
                category = "Food",
                percentage = "25%"
            )
            CategoryItem(
                icon = R.drawable.ic_bills,
                color = Color(0xFF3B82F6),
                category = "Bills",
                percentage = "20%"
            )
            CategoryItem(
                icon = R.drawable.ic_others,
                color = Color(0xFF10B981),
                category = "Others",
                percentage = "20%"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Transactions
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
                fontSize = 14.sp,
                color = Color(0xFF8D5CF5),
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction Items
        TransactionItem(
            icon = R.drawable.ic_shopping,
            color = Color(0xFF8D5CF5),
            title = "Shopping",
            subtitle = "Amazon.com",
            amount = "-$84.99",
            date = "Today"
        )
        
        TransactionItem(
            icon = R.drawable.ic_food,
            color = Color(0xFFF87171),
            title = "Food",
            subtitle = "Restaurant",
            amount = "-$32.50",
            date = "Yesterday"
        )
        
        TransactionItem(
            icon = R.drawable.ic_bills,
            color = Color(0xFF3B82F6),
            title = "Bills",
            subtitle = "Electricity",
            amount = "-$145.00",
            date = "Mar 15"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            containerColor = Color.Transparent
        ) {
            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = "Home",
                        tint = Color(0xFF8D5CF5)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF8D5CF5),
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_stats),
                        contentDescription = "Statistics"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF8D5CF5),
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wallet),
                        contentDescription = "Wallet"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF8D5CF5),
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF8D5CF5),
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun CategoryItem(
    icon: Int,
    color: Color,
    category: String,
    percentage: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = category,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = percentage,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = category,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun TransactionItem(
    icon: Int,
    color: Color,
    title: String,
    subtitle: String,
    amount: String,
    date: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = date,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
} 