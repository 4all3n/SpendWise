package com.fallen.spenwise.model

import androidx.compose.ui.graphics.Color

data class BudgetCategory(
    val name: String,
    val icon: Int,
    val spent: Double,
    val budget: Double,
    val color: Color
) 