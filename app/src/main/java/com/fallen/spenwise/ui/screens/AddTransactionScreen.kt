package com.fallen.spenwise.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fallen.spenwise.R
import com.fallen.spenwise.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.SelectableDates
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntSize
import com.fallen.spenwise.data.TransactionRepository
import com.fallen.spenwise.data.UserRepository
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,  // Callback for navigating back
    onSaveTransaction: (String, String, Double, String, Date, String?) -> Unit  // Callback for saving transaction
) {
    // State variables for form fields
    var selectedTab by remember { mutableStateOf(0) }  // 0 for Expense, 1 for Income
    var title by remember { mutableStateOf("") }  // Transaction title
    var amount by remember { mutableStateOf("") }  // Transaction amount
    var selectedCategory by remember { mutableStateOf("") }  // Selected category
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }  // Current date as default
    var note by remember { mutableStateOf("") }  // Optional note
    var isExpanded by remember { mutableStateOf(false) }  // State for category dropdown
    val scrollState = rememberScrollState()  // Scroll state for the form
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateState = remember { mutableStateOf(selectedDate) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val transactionRepository = remember { TransactionRepository(context) }
    val currentUserId = remember { userRepository.getCurrentUserId() }

    // Category dropdown positioning
    var dropdownWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    // Reset fields when switching between expense and income
    LaunchedEffect(selectedTab) {
        title = ""
        amount = ""
        selectedCategory = ""
        note = ""
        selectedDate = dateFormatter.format(Date())
    }

    // Define categories based on transaction type (Expense/Income)
    val categories = remember(selectedTab) {
        when (selectedTab) {
            0 -> listOf( // Expense categories with their icons
                "Food" to R.drawable.ic_food,
                "Shopping" to R.drawable.ic_shopping,
                "Bills" to R.drawable.ic_bills,
                "Entertainment" to R.drawable.ic_entertainment,
                "Transport" to R.drawable.ic_travel,
                "Others" to R.drawable.ic_others
            )
            else -> listOf( // Income categories with their icons
                "Salary" to R.drawable.ic_wallet,
                "Freelance" to R.drawable.ic_freelance,
                "Investment" to R.drawable.ic_investment,
                "Others" to R.drawable.ic_dollar
            )
        }
    }

    // Main container with gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1E27),  // Dark blue-gray
                        Color(0xFF232731)   // Slightly lighter blue-gray
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()  // Account for system status bar
        ) {
            // Top navigation bar with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2F3C))  // Dark background for button
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                // Screen title
                Text(
                    text = "Add Transaction",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // Empty spacer for symmetry
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Transaction type selector (Expense/Income tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color(0xFF2A2F3C))  // Dark background for tabs
            ) {
                listOf("Expense", "Income").forEachIndexed { index, text ->
                    val isSelected = selectedTab == index
                    // Animate background color change
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (index == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50)  // Purple for Expense, Green for Income
                        } else Color(0xFF2A2F3C),
                        animationSpec = tween(300),
                        label = "tabColor"
                    )

                    // Individual tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(21.dp))
                            .background(backgroundColor)
                            .clickable { selectedTab = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Scrollable form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title input field
                Text(
                    text = "Title",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter title", color = Color.White.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        cursorColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        unfocusedContainerColor = Color(0xFF2A2F3C),
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Amount input field with currency symbol
                Text(
                    text = "Amount",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00", color = Color.White.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        cursorColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        unfocusedContainerColor = Color(0xFF2A2F3C),
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Text(
                            text = "₹",
                            fontSize = 24.sp,
                            color = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Category selection field with dropdown
                Text(
                    text = "Category",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = true }
                            .onGloballyPositioned { coordinates ->
                                dropdownWidth = coordinates.size.width
                            },
                        trailingIcon = {
                            IconButton(onClick = { isExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select category",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedContainerColor = Color(0xFF2A2F3C),
                            focusedContainerColor = Color(0xFF2A2F3C),
                            unfocusedBorderColor = Color(0xFF2A2F3C),
                            focusedBorderColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50)
                        ),
                        placeholder = {
                            Text(
                                text = "Select Category",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Category dropdown menu
                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                        modifier = Modifier
                            .background(
                                color = Color(0xFF2A2F3C),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .width(with(density) { dropdownWidth.toDp() })
                            .offset(y = 4.dp)
                    ) {
                        categories.forEach { (category, icon) ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedCategory = category
                                    isExpanded = false
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        // Category icon with colored background
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (selectedTab == 0) 
                                                        Color(0xFF8D5CF5).copy(alpha = 0.2f) 
                                                    else 
                                                        Color(0xFF4CAF50).copy(alpha = 0.2f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = icon),
                                                contentDescription = category,
                                                tint = if (selectedTab == 0) 
                                                    Color(0xFF8D5CF5) 
                                                else 
                                                    Color(0xFF4CAF50),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        // Category name and type
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = category,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = if (selectedTab == 0) "Expense" else "Income",
                                                color = Color.White.copy(alpha = 0.5f),
                                                fontSize = 12.sp
                                            )
                                        }
                                        // Selection indicator
                                        if (selectedCategory == category) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (selectedTab == 0) 
                                                            Color(0xFF8D5CF5) 
                                                        else 
                                                            Color(0xFF4CAF50)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .background(
                                        if (selectedCategory == category) {
                                            if (selectedTab == 0) 
                                                Color(0xFF8D5CF5).copy(alpha = 0.1f) 
                                            else 
                                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                                        } else Color.Transparent
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date field (currently read-only)
                Text(
                    text = "Date",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    placeholder = { Text("Select Date", color = Color.White.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        cursorColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        unfocusedContainerColor = Color(0xFF2A2F3C),
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                // Date Picker Dialog
                if (showDatePicker) {
                    val calendar = Calendar.getInstance()
                    val today = calendar.timeInMillis

                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = dateFormatter.parse(selectedDate)?.time,
                        initialDisplayedMonthMillis = null,
                        yearRange = IntRange(2020, 2030),
                        initialDisplayMode = DisplayMode.Picker,
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis <= today
                            }
                        }
                    )
                    
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        if (millis <= today) {
                                            selectedDate = dateFormatter.format(Date(millis))
                                        }
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text(
                                    "OK",
                                    color = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text(
                                    "Cancel",
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            colors = DatePickerDefaults.colors(
                                containerColor = Color(0xFF2A2F3C),
                                titleContentColor = Color.White,
                                headlineContentColor = Color.White,
                                weekdayContentColor = Color.White.copy(alpha = 0.7f),
                                subheadContentColor = Color.White,
                                navigationContentColor = Color.White,
                                yearContentColor = Color.White,
                                currentYearContentColor = Color.White,
                                selectedYearContentColor = Color.White,
                                selectedYearContainerColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                                dayContentColor = Color.White,
                                selectedDayContentColor = Color.White,
                                selectedDayContainerColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                                todayContentColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                                todayDateBorderColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Optional note field
                Text(
                    text = "Note (Optional)",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Add note", color = Color.White.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        cursorColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50),
                        unfocusedContainerColor = Color(0xFF2A2F3C),
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save button
                Button(
                    onClick = {
                        if (title.isNotEmpty() && amount.isNotEmpty() && selectedCategory.isNotEmpty()) {
                            val transactionAmount = amount.toDoubleOrNull() ?: 0.0
                            val transactionDate = dateFormatter.parse(selectedDate)?.time?.let { Date(it) } ?: Date()
                            val transactionNote = note.takeIf { it.isNotEmpty() }
                            
                            if (currentUserId != null) {
                                // Save to local database
                                val result = if (selectedTab == 0) {
                                    transactionRepository.addExpense(
                                        currentUserId,
                                        title,
                                        transactionAmount,
                                        selectedCategory,
                                        transactionDate,
                                        transactionNote
                                    )
                                } else {
                                    transactionRepository.addIncome(
                                        currentUserId,
                                        title,
                                        transactionAmount,
                                        selectedCategory,
                                        transactionDate,
                                        transactionNote
                                    )
                                }
                                
                                if (result > 0) {
                                    // Call the original callback for any additional processing
                                    onSaveTransaction(
                                        title,
                                        amount,
                                        transactionAmount,
                                        selectedCategory,
                                        transactionDate,
                                        transactionNote
                                    )
                                    
                                    // Show success message
                                    Toast.makeText(
                                        context,
                                        "Transaction saved successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Show error message
                                    Toast.makeText(
                                        context,
                                        "Failed to save transaction",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // User not logged in
                                Toast.makeText(
                                    context,
                                    "Please log in to save transactions",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Show validation error
                            Toast.makeText(
                                context,
                                "Please fill all required fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) Color(0xFF8D5CF5) else Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Save Transaction",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Snackbar for showing messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF2A2F3C),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
} 