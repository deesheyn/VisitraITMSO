package com.visitraitmso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val repository    = FirestoreVisitRepository()

                NavHost(
                    navController    = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(navController)
                    }

                    composable("register") {
                        val viewModel: RegisterViewModel = viewModel {
                            RegisterViewModel(repository)
                        }
                        RegisterScreen(navController, viewModel)
                    }

                    composable("login") {
                        val viewModel: VisitViewModel = viewModel {
                            VisitViewModel(repository)
                        }
                        LoginVisitScreen(navController, viewModel)
                    }

                    composable("dashboard") {
                        AdminScreen(navController, repository)
                    }

                    composable("role_select") {
                        RoleSelectorScreen(navController, repository)
                    }

                    composable("staff_login") {
                        val viewModel: StaffViewModel = viewModel {
                            StaffViewModel(repository)
                        }
                        StaffLoginScreen(navController, viewModel)
                    }

                    composable("staff_dashboard") {
                        val viewModel: StaffViewModel = viewModel {
                            StaffViewModel(repository)
                        }
                        StaffDashboardScreen(navController, viewModel)
                    }

                    composable("monitor") {
                        MonitorScreen(navController, repository)
                    }
                }
            }
        }
    }
}