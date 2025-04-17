package com.fallen.spenwise.navigation

import android.util.Log
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
            BudgetScreen(
                onNavigate = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                        1 -> navController.navigate(Screen.Transactions.route) {
                            popUpTo(Screen.Transactions.route) { inclusive = true }
                        }
                        3 -> navController.navigate(Screen.Settings.route) {
                            popUpTo(Screen.Settings.route) { inclusive = true }
                        }
                    }
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