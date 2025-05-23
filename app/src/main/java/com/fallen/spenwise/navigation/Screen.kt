package com.fallen.spenwise.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Budget : Screen("budget")
    object Settings : Screen("settings")
    object AddTransaction : Screen("add_transaction")
    object Transactions : Screen("transactions")
    object AddBudget : Screen("add_budget")
    object EditBudget : Screen("edit_budget")
    object EditTransaction : Screen("edit_transaction")
    object ChangePassword : Screen("change_password")
} 