package com.adityacodes.financebuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adityacodes.financebuddy.Screens.AuthScreen
import com.adityacodes.financebuddy.Screens.ChatScreen
import com.adityacodes.financebuddy.Screens.GoalScreen
import com.adityacodes.financebuddy.Screens.HistoryScreen
import com.adityacodes.financebuddy.Screens.TransactionScreen
import com.adityacodes.financebuddy.viewmodels.AuthState
import com.adityacodes.financebuddy.viewmodels.AuthViewModel
import com.adityacodes.financebuddy.viewmodels.ChatViewModel
import com.adityacodes.financebuddy.viewmodels.GoalViewModel
import com.adityacodes.financebuddy.viewmodels.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceBuddyTheme {
                FinanceBuddyApp()
            }
        }
    }
}


@Composable
fun FinanceBuddyApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) "chat" else "auth"
    ) {
        composable("auth") {
            AuthScreen(
                authViewModel = authViewModel,
                onNavigateToChat = {
                    navController.navigate("chat") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("chat") {
            val chatViewModel: ChatViewModel = hiltViewModel()
            ChatScreen(
                chatViewModel = chatViewModel,
                authViewModel = authViewModel,
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                navController = navController
            )
        }

        composable("history") {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                historyViewModel = historyViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("goals") {
            val goalViewModel: GoalViewModel = hiltViewModel()
            GoalScreen(
                viewModel = goalViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
             // new goals screen
        }
        composable("transactions") {

            TransactionScreen(navController = navController) // new transactions screen
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Authenticated -> {
                if (navController.currentDestination?.route == "auth") {
                    navController.navigate("chat") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun FinanceBuddyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}