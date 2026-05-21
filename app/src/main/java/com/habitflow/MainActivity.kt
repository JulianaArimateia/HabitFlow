package com.habitflow

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.habitflow.ui.navigation.NavGraph
import com.habitflow.ui.navigation.Screen
import com.habitflow.ui.theme.HabitFlowTheme
import com.habitflow.util.AuthManager

class MainActivity : ComponentActivity() {

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission result handled silently */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            HabitFlowTheme {
                val navController = rememberNavController()
                val authManager = remember { AuthManager(applicationContext) }
                val start = if (authManager.isLoggedIn()) Screen.Home.route else Screen.Login.route

                NavGraph(
                    navController = navController,
                    startDestination = start
                )
            }
        }
    }
}
