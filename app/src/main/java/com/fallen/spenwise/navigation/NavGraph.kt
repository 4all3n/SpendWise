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
import com.fallen.spenwise.ui.screens.DashboardScreen
import com.fallen.spenwise.ui.screens.LoginScreen
import com.fallen.spenwise.ui.screens.SignUpScreen
import com.fallen.spenwise.ui.screens.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    initialDestination: String = Screen.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                onSignInClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSignInClick = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
} 