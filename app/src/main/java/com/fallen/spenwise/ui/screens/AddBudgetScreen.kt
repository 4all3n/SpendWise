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
import com.fallen.spenwise.data.BudgetRepository
import com.fallen.spenwise.data.UserRepository
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    onNavigateBack: () -> Unit,
    onSaveBudget: (String, Double, Date, Date) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Food & Dining") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { 
        mutableStateOf(Calendar.getInstance().apply {
            time = Date()
            add(Calendar.MONTH, 1)
        }.time)
    }
    var budgetLimit by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Category dropdown positioning
    var dropdownWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    
    // Define categories
    val categories = remember {
        listOf(
            "Food & Dining" to R.drawable.ic_food,
            "Travel" to R.drawable.ic_travel,
            "Shopping" to R.drawable.ic_shopping,
            "Entertainment" to R.drawable.ic_entertainment,
            "Bills" to R.drawable.ic_bills,
            "Others" to R.drawable.ic_others
        )
    }
    
    var isExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val budgetRepository = remember { BudgetRepository(context) }
    val currentUserId = remember { userRepository.getCurrentUserId() }
    
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
                    text = "Add Budget",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // Empty spacer for symmetry
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Scrollable form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

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
                            focusedBorderColor = Color(0xFF8D5CF5)
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
                                                .background(Color(0xFF8D5CF5).copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = icon),
                                                contentDescription = category,
                                                tint = Color(0xFF8D5CF5),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        // Category name
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = category,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        // Selection indicator
                                        if (selectedCategory == category) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF8D5CF5)),
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
                                            Color(0xFF8D5CF5).copy(alpha = 0.1f)
                                        } else Color.Transparent
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Start Date Field
                Text(
                    text = "Start Date",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = dateFormatter.format(startDate),
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = Color(0xFF8D5CF5),
                        cursorColor = Color(0xFF8D5CF5),
                        unfocusedContainerColor = Color(0xFF2A2F3C),
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // End Date Field
                Text(
                    text = "End Date",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = dateFormatter.format(endDate),
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEndDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = Color(0xFF8D5CF5),
                        cursorColor = Color(0xFF8D5CF5),
                        unfocusedContainerColor = Color(0xFF2A2F3C),
                        focusedContainerColor = Color(0xFF2A2F3C)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Budget Limit Field
                Text(
                    text = "Budget Limit",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = budgetLimit,
                    onValueChange = { budgetLimit = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00", color = Color.White.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF2A2F3C),
                        focusedBorderColor = Color(0xFF8D5CF5),
                        cursorColor = Color(0xFF8D5CF5),
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

                Spacer(modifier = Modifier.height(32.dp))

                // Save button
                Button(
                    onClick = {
                        val limit = budgetLimit.toDoubleOrNull() ?: 0.0
                        if (selectedCategory.isEmpty()) {
                            Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (budgetLimit.isEmpty()) {
                            Toast.makeText(context, "Please enter a budget limit", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        android.util.Log.d("AddBudgetScreen", "Saving budget: category=$selectedCategory, limit=$limit, startDate=$startDate, endDate=$endDate")
                        onSaveBudget(selectedCategory, limit, startDate, endDate)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8D5CF5)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Save Budget",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Date Picker Dialog for Start Date
        if (showStartDatePicker) {
            val calendar = Calendar.getInstance()
            val today = calendar.timeInMillis

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = startDate.time,
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
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                if (millis <= today) {
                                    startDate = Date(millis)
                                }
                            }
                            showStartDatePicker = false
                        }
                    ) {
                        Text(
                            "OK",
                            color = Color(0xFF8D5CF5)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showStartDatePicker = false }
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
                        selectedYearContainerColor = Color(0xFF8D5CF5),
                        dayContentColor = Color.White,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = Color(0xFF8D5CF5),
                        todayContentColor = Color(0xFF8D5CF5),
                        todayDateBorderColor = Color(0xFF8D5CF5)
                    )
                )
            }
        }

        // Date Picker Dialog for End Date
        if (showEndDatePicker) {
            val calendar = Calendar.getInstance()
            val today = calendar.timeInMillis

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = endDate.time,
                initialDisplayedMonthMillis = null,
                yearRange = IntRange(2020, 2030),
                initialDisplayMode = DisplayMode.Picker
            )
            
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                endDate = Date(millis)
                            }
                            showEndDatePicker = false
                        }
                    ) {
                        Text(
                            "OK",
                            color = Color(0xFF8D5CF5)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEndDatePicker = false }
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
                        selectedYearContainerColor = Color(0xFF8D5CF5),
                        dayContentColor = Color.White,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = Color(0xFF8D5CF5),
                        todayContentColor = Color(0xFF8D5CF5),
                        todayDateBorderColor = Color(0xFF8D5CF5)
                    )
                )
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