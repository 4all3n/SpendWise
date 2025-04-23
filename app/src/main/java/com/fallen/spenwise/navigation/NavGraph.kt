package com.fallen.spenwise.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import com.fallen.spenwise.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.fallen.spenwise.data.BudgetRepository
import androidx.compose.ui.platform.LocalContext
import com.fallen.spenwise.navigation.Screen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(Screen.SignUp.route) },
                onSignInClick = { navController.navigate(Screen.Login.route) }
            )
        }
        
        composable(route = Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = { 
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Welcome.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Dashboard.route, navOptions)
                }
            )
        }
        
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate(Screen.Login.route) },
                onSignUpSuccess = { 
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Welcome.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Dashboard.route, navOptions)
                }
            )
        }
        
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onBudgetClick = {
                    navController.navigate(Screen.Budget.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route)
                },
                onNavigateToEditTransaction = { transactionId, isExpense ->
                    navController.navigate("${Screen.EditTransaction.route}/$transactionId/$isExpense")
                }
            )
        }

        composable(route = Screen.Budget.route) {
            val context = LocalContext.current
            BudgetScreen(
                onNavigateBack = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Dashboard.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Dashboard.route, navOptions)
                },
                onNavigateToAddBudget = {
                    navController.navigate(Screen.AddBudget.route)
                },
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToEditBudget = { category, limit ->
                    navController.navigate("${Screen.EditBudget.route}/$category/$limit")
                },
                context = context
            )
        }

        composable(route = Screen.AddBudget.route) {
            val context = LocalContext.current
            AddBudgetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveBudget = { category, limit, startDate, endDate ->
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val budgetRepository = BudgetRepository(context)
                        try {
                            android.util.Log.d("NavGraph", "Adding budget for user ${currentUser.uid}: category=$category, limit=$limit")
                            val result = budgetRepository.addBudget(
                                currentUser.uid,
                                category,
                                limit,
                                startDate,
                                endDate
                            )
                            android.util.Log.d("NavGraph", "Budget added successfully with ID: $result")
                            Toast.makeText(context, "Budget added successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            android.util.Log.e("NavGraph", "Error saving budget: ${e.message}", e)
                            Toast.makeText(context, "Error saving budget: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.util.Log.e("NavGraph", "No user logged in")
                        Toast.makeText(context, "You must be logged in to add a budget", Toast.LENGTH_SHORT).show()
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Dashboard.route, inclusive = false)
                        .build()
                    navController.navigate(Screen.Dashboard.route, navOptions)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onNavigateToTransactions = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Settings.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Transactions.route, navOptions)
                },
                onBudgetClick = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Settings.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Budget.route, navOptions)
                },
                onSignOut = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(route = Screen.Welcome.route, inclusive = true)
                        .build()
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Welcome.route, navOptions)
                }
            )
        }

        composable(route = Screen.AddTransaction.route) {
            val navigatedFrom = navController.previousBackStackEntry?.destination?.route
            AddTransactionScreen(
                onNavigateBack = {
                    if (navigatedFrom == Screen.Transactions.route) {
                        navController.navigate(Screen.Transactions.route) {
                            popUpTo(Screen.Transactions.route) { inclusive = true }
                        }
                    } else {
                    navController.popBackStack()
                    }
                },
                onSaveTransaction = { type, title, amount, category, date, note ->
                    if (navigatedFrom == Screen.Transactions.route) {
                        navController.navigate(Screen.Transactions.route) {
                            popUpTo(Screen.Transactions.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                navigatedFrom = if (navigatedFrom == Screen.Dashboard.route) "dashboard" else "transactions"
            )
        }

        composable(route = Screen.Transactions.route) {
            TransactionScreen(
                onNavigateToHome = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Dashboard.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Dashboard.route, navOptions)
                },
                onNavigateToStats = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Budget.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Budget.route, navOptions)
                },
                onNavigateToSettings = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Settings.route, inclusive = true)
                        .build()
                    navController.navigate(Screen.Settings.route, navOptions)
                },
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToEditTransaction = { transactionId, isExpense ->
                    navController.navigate("${Screen.EditTransaction.route}/$transactionId/$isExpense")
                }
            )
        }

        // Add the EditBudget route
        composable(
            route = "${Screen.EditBudget.route}/{category}/{limit}"
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val limit = backStackEntry.arguments?.getString("limit")?.toDoubleOrNull() ?: 0.0
            val context = LocalContext.current
            
            EditBudgetScreen(
                categoryName = category,
                currentLimit = limit,
                onNavigateBack = {
                    navController.popBackStack()
                },
                context = context
            )
        }

        // Add the EditTransaction route
        composable(
            route = "${Screen.EditTransaction.route}/{transactionId}/{isExpense}"
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toIntOrNull() ?: 0
            val isExpense = backStackEntry.arguments?.getString("isExpense")?.toBoolean() ?: false
            val context = LocalContext.current
            
            EditTransactionScreen(
                transactionId = transactionId,
                isExpense = isExpense,
                onNavigateBack = {
                    navController.popBackStack()
                },
                context = context
            )
        }

        // Add the ChangePassword route
        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 