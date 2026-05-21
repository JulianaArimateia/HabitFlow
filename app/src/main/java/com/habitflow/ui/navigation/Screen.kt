package com.habitflow.ui.navigation

sealed class Screen(val route: String) {
    object Login        : Screen("login")
    object Register     : Screen("register")
    object Home         : Screen("home")
    object Dashboard    : Screen("dashboard")
    object AddHabit     : Screen("add_habit")
    object EditHabit    : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }
}
