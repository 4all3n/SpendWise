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
import com.fallen.spenwise.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.fallen.spenwise.data.BudgetRepository
import androidx.compose.ui.platform.LocalContext

@Composable
fun NavGraph(
    navController: NavHostController,
    initialDestination: String = Screen.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = initialDestination
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
                onLoginSuccess = { navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }}
            )
        }
        
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate(Screen.Login.route) },
                onSignUpSuccess = { navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }}
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
                }
            )
        }

        composable(route = Screen.Budget.route) {
            val context = LocalContext.current
            BudgetScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
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
                onNavigate = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                        1 -> navController.navigate(Screen.Transactions.route) {
                            popUpTo(Screen.Transactions.route) { inclusive = true }
                        }
                        2 -> navController.navigate(Screen.Budget.route) {
                            popUpTo(Screen.Budget.route) { inclusive = true }
                        }
                    }
                },
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveTransaction = { type, title, amount, category, date, note ->
                    // TODO: Handle saving transaction
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Transactions.route) {
            TransactionScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Budget.route) {
                        popUpTo(Screen.Budget.route) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                }
            )
        }
    }
} 