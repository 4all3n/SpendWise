package com.fallen.spenwise.model

import androidx.compose.ui.graphics.Color

data class Transaction(
    val type: TransactionType,
    val color: Color,
    val title: String,
    val subtitle: String,
    val amount: String,
    val date: String
) 