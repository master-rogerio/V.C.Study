package com.example.study.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.study.ui.screens.*
import com.example.study.ui.theme.VCStudyTheme

@Composable
fun StudyApp() {
    VCStudyTheme {
        // Use o NavController padrão
        val navController = rememberNavController()
        StudyNavigation(navController = navController)
    }
}

@Composable
private fun StudyNavigation(
    navController: NavHostController
) {
    val tabRoutes = listOf("home", "decks", "exercise_selection", "environments", "ai_assistant")
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var previousRoute by remember { mutableStateOf("home") }
    val currentRoute = currentBackStackEntry?.destination?.route?.split("/")?.firstOrNull() ?: "home"

    LaunchedEffect(currentRoute) {
        previousRoute = currentRoute
    }

    // Use o NavHost padrão, que agora suporta animações
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            val targetRoute = targetState.destination.route?.split("/")?.firstOrNull()
            val initialRoute = initialState.destination.route?.split("/")?.firstOrNull()

            if (targetRoute in tabRoutes && initialRoute in tabRoutes) {
                val targetIndex = tabRoutes.indexOf(targetRoute)
                val initialIndex = tabRoutes.indexOf(initialRoute)
                val movingRight = targetIndex > initialIndex

                slideInHorizontally(
                    initialOffsetX = { if (movingRight) it else -it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(200))
            } else {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(200))
            }
        },
        exitTransition = {
            val targetRoute = targetState.destination.route?.split("/")?.firstOrNull()
            val initialRoute = initialState.destination.route?.split("/")?.firstOrNull()

            if (targetRoute in tabRoutes && initialRoute in tabRoutes) {
                val targetIndex = tabRoutes.indexOf(targetRoute)
                val initialIndex = tabRoutes.indexOf(initialRoute)
                val movingRight = targetIndex > initialIndex

                slideOutHorizontally(
                    targetOffsetX = { if (movingRight) -it else it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(200))
            } else {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(200))
            }
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(200))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(200))
        }
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToDecks = { navController.navigate("decks") { launchSingleTop = true } },
                onNavigateToExercise = { navController.navigate("exercise_selection") { launchSingleTop = true } },
                onNavigateToEnvironments = { navController.navigate("environments") { launchSingleTop = true } },
                onNavigateToAI = { navController.navigate("ai_assistant") { launchSingleTop = true } }
            )
        }

        composable("decks") {
            DecksScreen(
                onNavigateToFlashcards = { deckId, deckName -> navController.navigate("flashcards/$deckId/$deckName") },
                onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onNavigateToExercise = { navController.navigate("exercise_selection") { launchSingleTop = true } },
                onNavigateToEnvironments = { navController.navigate("environments") { launchSingleTop = true } },
                onNavigateToAI = { navController.navigate("ai_assistant") { launchSingleTop = true } }
            )
        }

        composable("flashcards/{deckId}/{deckName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: -1L
            val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
            FlashcardsScreen(
                deckId = deckId,
                deckName = deckName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToExercise = { navController.navigate("exercise/$deckId/$deckName") }
            )
        }

        composable("exercise_selection") {
            ExerciseSelectionScreen(
                onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onNavigateToDecks = { navController.navigate("decks") { launchSingleTop = true } },
                onNavigateToEnvironments = { navController.navigate("environments") { launchSingleTop = true } },
                onNavigateToAI = { navController.navigate("ai_assistant") { launchSingleTop = true } },
                onNavigateToExercise = { deckId, deckName -> navController.navigate("exercise/$deckId/$deckName") }
            )
        }

        composable("exercise/{deckId}/{deckName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: -1L
            val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
            ExerciseScreen(
                deckId = deckId,
                deckName = deckName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResults = { score, total ->
                    navController.navigate("exercise_results/$score/$total") {
                        popUpTo("exercise_selection")
                    }
                }
            )
        }

        composable("exercise_results/{score}/{total}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            PlaceholderResultsScreen(
                score = score,
                total = total,
                onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onNavigateToDecks = { navController.navigate("decks") { popUpTo("home"); launchSingleTop = true } },
                onRetryExercise = { navController.popBackStack() }
            )
        }

        composable("environments") {
            EnvironmentsScreen(
                onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onNavigateToDecks = { navController.navigate("decks") { launchSingleTop = true } },
                onNavigateToExercise = { navController.navigate("exercise_selection") { launchSingleTop = true } }
            )
        }

        composable("ai_assistant") {
            PlaceholderScreen(
                title = "Assistente IA",
                subtitle = "O assistente de IA para seus estudos.",
                onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onNavigateToDecks = { navController.navigate("decks") { launchSingleTop = true } },
                onNavigateToExercise = { navController.navigate("exercise_selection") { launchSingleTop = true } },
                onNavigateToEnvironments = { navController.navigate("environments") { launchSingleTop = true } },
                selectedTab = 4
            )
        }
    }
}