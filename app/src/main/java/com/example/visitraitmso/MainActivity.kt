package com.visitraitmso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    composable("home") {
                        HomeScreen(navController)
                    }

                    composable("register") {
                        RegisterScreen(navController)
                    }

                    composable("login") {
                        LoginVisitScreen(navController)
                    }

                    // 🔥 FIX: USE AdminScreen (NOT AdminDashboardScreen)
                    composable("dashboard") {
                        AdminScreen()
                    }
                }
            }
        }
    }
}