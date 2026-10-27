package com.example.activitytrackerai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) "login" else "main"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    currentUser = FirebaseAuth.getInstance().currentUser
                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    currentUser = FirebaseAuth.getInstance().currentUser
                    navController.navigate("main") { popUpTo("register") { inclusive = true } }
                },
                onNavigateToLogin = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }
            )
        }
        composable("main") {
            MainScreen(
                onSignOut = {
                    currentUser = null
                    navController.navigate("login") { popUpTo("main") { inclusive = true } }
                }
            )
        }
    }
}