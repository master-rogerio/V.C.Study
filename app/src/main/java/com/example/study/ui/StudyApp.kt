package com.example.study.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
        val navController = rememberNavController()
        StudyNavigation(navController = navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StudyNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 1000 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -1000 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToDecks = {
                    navController.navigate("decks") {
                        launchSingleTop = true
                    }
                },
                onNavigateToExercise = {
                    navController.navigate("exercise_selection") {
                        launchSingleTop = true
                    }
                },
                onNavigateToEnvironments = {
                    navController.navigate("environments") {
                        launchSingleTop = true
                    }
                },
                onNavigateToAI = {
                    navController.navigate("ai_assistant") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("decks") {
            DecksScreen(
                onNavigateToFlashcards = { deckId, deckName ->
                    navController.navigate("flashcards/$deckId/$deckName")
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToExercise = {
                    navController.navigate("exercise_selection") {
                        launchSingleTop = true
                    }
                },
                onNavigateToEnvironments = {
                    navController.navigate("environments") {
                        launchSingleTop = true
                    }
                },
                onNavigateToAI = {
                    navController.navigate("ai_assistant") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("flashcards/{deckId}/{deckName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: -1L
            val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
            
            FlashcardsScreen(
                deckId = deckId,
                deckName = deckName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToExercise = {
                    navController.navigate("exercise/$deckId/$deckName")
                }
            )
        }

        composable("exercise_selection") {
            com.example.study.ui.screens.ExerciseSelectionScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToDecks = {
                    navController.navigate("decks") {
                        launchSingleTop = true
                    }
                },
                onNavigateToEnvironments = {
                    navController.navigate("environments") {
                        launchSingleTop = true
                    }
                },
                onNavigateToAI = {
                    navController.navigate("ai_assistant") {
                        launchSingleTop = true
                    }
                },
                onNavigateToExercise = { deckId, deckName ->
                    navController.navigate("exercise/$deckId/$deckName")
                }
            )
        }

        composable("exercise/{deckId}/{deckName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: -1L
            val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
            
            com.example.study.ui.screens.ExerciseScreen(
                deckId = deckId,
                deckName = deckName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResults = { score, total ->
                    navController.navigate("exercise_results/$score/$total") {
                        popUpTo("exercise_selection") { inclusive = false }
                    }
                }
            )
        }

        composable("exercise_results/{score}/{total}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            
            ExerciseResultsScreen(
                score = score,
                total = total,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToDecks = {
                    navController.navigate("decks") {
                        popUpTo("decks") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRetryExercise = {
                    navController.popBackStack()
                }
            )
        }

        composable("environments") {
            com.example.study.ui.screens.EnvironmentsScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToDecks = {
                    navController.navigate("decks") {
                        launchSingleTop = true
                    }
                },
                onNavigateToExercise = {
                    navController.navigate("exercise_selection") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("ai_assistant") {
            com.example.study.ui.screens.AIAssistantScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToDecks = {
                    navController.navigate("decks") {
                        launchSingleTop = true
                    }
                },
                onNavigateToExercise = {
                    navController.navigate("exercise_selection") {
                        launchSingleTop = true
                    }
                },
                onNavigateToEnvironments = {
                    navController.navigate("environments") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// Placeholder screens para completar a navegação
@Composable
private fun ExerciseSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onNavigateToEnvironments: () -> Unit,
    onNavigateToExercise: (Long, String) -> Unit
) {
    // TODO: Implementar tela de seleção de exercícios
    PlaceholderScreen(
        title = "Exercícios",
        subtitle = "Selecione um deck para praticar",
        onNavigateToHome = onNavigateToHome,
        onNavigateToDecks = onNavigateToDecks,
        onNavigateToExercise = { /* TODO */ },
        onNavigateToEnvironments = onNavigateToEnvironments,
        selectedTab = 2
    )
}


@Composable
private fun ExerciseResultsScreen(
    score: Int,
    total: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onRetryExercise: () -> Unit
) {
    // TODO: Implementar tela de resultados
    PlaceholderResultsScreen(
        score = score,
        total = total,
        onNavigateToHome = onNavigateToHome,
        onNavigateToDecks = onNavigateToDecks,
        onRetryExercise = onRetryExercise
    )
}

