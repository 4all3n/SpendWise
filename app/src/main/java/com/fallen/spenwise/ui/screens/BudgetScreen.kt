package com.fallen.spenwise.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import com.fallen.spenwise.model.BudgetCategory

@Composable
fun BudgetScreen(
    onAddCategory: () -> Unit = {}
) {
    var selectedMonth by remember { mutableStateOf("March 2025") }
    
    // Sample data
    val categories = remember {
        listOf(
            BudgetCategory(
                name = "Food & Dining",
                icon = R.drawable.ic_food,
                spent = 450.0,
                budget = 600.0,
                color = Color(0xFF8B5CF6)
            ),
            BudgetCategory(
                name = "Travel",
                icon = R.drawable.ic_travel,
                spent = 800.0,
                budget = 1000.0,
                color = Color(0xFF10B981)
            ),
            BudgetCategory(
                name = "Shopping",
                icon = R.drawable.ic_shopping,
                spent = 920.0,
                budget = 800.0,
                color = Color(0xFFEF4444)
            ),
            BudgetCategory(
                name = "Entertainment",
                icon = R.drawable.ic_entertainment,
                spent = 150.0,
                budget = 300.0,
                color = Color(0xFF8B5CF6)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1E27))
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "Budget Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )

        // Month selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF232731))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Previous month */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_left),
                    contentDescription = "Previous month",
                    tint = Color.White
                )
            }
            
            Text(
                text = selectedMonth,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            IconButton(onClick = { /* Next month */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Next month",
                    tint = Color.White
                )
            }
        }

        // Add Category Button
        Button(
            onClick = onAddCategory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6)
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Category",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        // Categories List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category = category)
            }
        }
    }
}

@Composable
fun CategoryCard(category: BudgetCategory) {
    var showMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF232731))
            .padding(16.dp)
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
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(category.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = category.icon),
                        contentDescription = category.name,
                        tint = category.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // Menu Icon
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "More options",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color(0xFF2A2F3C))
            ) {
                DropdownMenuItem(
                    text = { Text("Edit", color = Color.White) },
                    onClick = { showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = Color.Red) },
                    onClick = { showMenu = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        val progress = (category.spent / category.budget).toFloat().coerceIn(0f, 1f)
        val progressColor = when {
            progress >= 1f -> Color.Red
            progress >= 0.8f -> Color(0xFFFFA500)
            else -> category.color
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = progressColor,
            trackColor = Color.White.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Spent and Budget Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spent: $${category.spent.toInt()}",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "Budget: $${category.budget.toInt()}",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

data class BudgetCategory(
    val name: String,
    val icon: Int,
    val spent: Double,
    val budget: Double,
    val color: Color
) 