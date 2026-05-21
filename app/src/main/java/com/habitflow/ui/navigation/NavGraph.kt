package com.habitflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.habitflow.ui.screens.auth.LoginScreen
import com.habitflow.ui.screens.auth.RegisterScreen
import com.habitflow.ui.screens.dashboard.DashboardScreen
import com.habitflow.ui.screens.habit.AddEditHabitScreen
import com.habitflow.ui.screens.home.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                onAddHabit = { navController.navigate(Screen.AddHabit.route) },
                onEditHabit = { id -> navController.navigate(Screen.EditHabit.createRoute(id)) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                }},
                onAddHabit = { navController.navigate(Screen.AddHabit.route) }
            )
        }

        composable(Screen.AddHabit.route) {
            AddEditHabitScreen(
                habitId = null,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId")
            AddEditHabitScreen(
                habitId = habitId,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
