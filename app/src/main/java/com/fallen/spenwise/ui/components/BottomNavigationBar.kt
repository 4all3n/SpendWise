package com.fallen.spenwise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fallen.spenwise.R

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF1B1E27),
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    icon = R.drawable.ic_home,
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )
                NavItem(
                    icon = R.drawable.ic_stats,
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )
                NavItem(
                    icon = R.drawable.ic_wallet,
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
                )
                NavItem(
                    icon = R.drawable.ic_settings,
                    isSelected = selectedTab == 3,
                    onClick = { onTabSelected(3) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit
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
            .clickable(onClick = onClick),
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