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
fun StudyApp(
    intelligentRotation: Boolean = false,
    preferredCardTypes: List<String> = emptyList(),
    locationId: String? = null,
    source: String? = null,
    locationName: String? = null
) {
    VCStudyTheme {
        val navController = rememberNavController()
        StudyNavigation(
            navController = navController,
            intelligentRotation = intelligentRotation,
            preferredCardTypes = preferredCardTypes,
            locationId = locationId,
            source = source,
            locationName = locationName
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StudyNavigation(
    navController: NavHostController,
    intelligentRotation: Boolean = false,
    preferredCardTypes: List<String> = emptyList(),
    locationId: String? = null,
    source: String? = null,
    locationName: String? = null
) {
    // Track tab order for directional animations
    val tabRoutes = listOf("home", "decks", "exercise_selection", "environments", "ai_assistant")
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var previousRoute by remember { mutableStateOf("home") }
    
    val currentRoute = currentBackStackEntry?.destination?.route?.split("/")?.firstOrNull() ?: "home"

    // Navegação inteligente baseada em geofencing
    LaunchedEffect(intelligentRotation, source, locationId) {
        if (intelligentRotation && source != null && locationId != null) {
            when (source) {
                "geofence", "location_service" -> {
                    // Navegar diretamente para a seleção de exercícios com rotação inteligente
                    navController.navigate("exercise_selection") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
    // Determine animation direction based on tab indices
    val isMovingForward = remember(currentRoute) {
        val currentIndex = tabRoutes.indexOf(currentRoute)
        val previousIndex = tabRoutes.indexOf(previousRoute)
        val result = if (currentIndex != -1 && previousIndex != -1) {
            currentIndex > previousIndex
        } else true // Default to forward for non-tab routes
        previousRoute = currentRoute
        result
    }

    // Otimização de animações para melhor performance
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
                    animationSpec = tween(100) // Reduzido de 300 para 100
                ) + fadeIn(animationSpec = tween(150)) // Reduzido de 200 para 150
            } else {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(200) // Reduzido de 300 para 200
                ) + fadeIn(animationSpec = tween(150)) // Reduzido de 200 para 150
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
                    animationSpec = tween(200) // Reduzido de 300 para 200
                ) + fadeOut(animationSpec = tween(150)) // Reduzido de 200 para 150
            } else {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(200) // Reduzido de 300 para 200
                ) + fadeOut(animationSpec = tween(150)) // Reduzido de 200 para 150
            }
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(200) // Reduzido de 300 para 200
            ) + fadeIn(animationSpec = tween(150)) // Reduzido de 200 para 150
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = tween(200) // Reduzido de 300 para 200
            ) + fadeOut(animationSpec = tween(150)) // Reduzido de 200 para 150
        }
    ) {


    /* Correção de animações
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
     */
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

        // Navegação inteligente para exercícios
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
                },
                intelligentRotation = intelligentRotation,
                preferredCardTypes = preferredCardTypes,
                locationId = locationId,
                source = source,
                locationName = locationName
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


        composable("exercise/{deckId}/{deckName}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: -1L
            val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
            ExerciseScreen(
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
            PlaceholderResultsScreen(
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
            EnvironmentsScreen(
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
                onNavigateToSpatialAnalytics = {
                    navController.navigate("spatial_analytics") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("spatial_analytics") {
            SpatialAnalyticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("ai_assistant") {
            PlaceholderScreen(
                title = "Assistente IA",
                subtitle = "O assistente de IA para seus estudos.",
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
                },
                selectedTab = 4
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

