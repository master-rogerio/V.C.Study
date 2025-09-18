package com.example.study.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000V\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0003\u001aN\u0010\n\u001a\u00020\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f2\u0018\u0010\r\u001a\u0014\u0012\u0004\u0012\u00020\u0003\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000f0\u000e2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\b\u001a\u00020\tH\u0003\u001ax\u0010\u0012\u001a\u00020\u00012\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0018\u0010\u0017\u001a\u0014\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u00182\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001eH\u0007\u001a0\u0010\u001f\u001a\u00020\u00012\u0006\u0010 \u001a\u00020\u00052\u0006\u0010!\u001a\u00020\u00052\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0003\u001a.\u0010#\u001a\u00020\u00012\u0006\u0010$\u001a\u00020\u001a2\u0012\u0010%\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\b\u001a\u00020\tH\u0003\u00a8\u0006&"}, d2 = {"DeckExerciseItem", "", "deck", "Lcom/example/study/data/Deck;", "dueCount", "", "onClick", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "DeckExerciseList", "decks", "", "deckStats", "", "Landroidx/compose/runtime/MutableState;", "onDeckClick", "Lkotlin/Function1;", "ExerciseSelectionScreen", "onNavigateToHome", "onNavigateToDecks", "onNavigateToEnvironments", "onNavigateToAI", "onNavigateToExercise", "Lkotlin/Function2;", "", "", "deckViewModel", "Lcom/example/study/ui/view/DeckViewModel;", "flashcardViewModel", "Lcom/example/study/ui/view/FlashcardViewModel;", "ExerciseStatsCard", "totalDueCards", "totalDecks", "onStartMixedExercise", "SearchBar", "query", "onQueryChange", "app_debug"})
public final class ExerciseSelectionScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ExerciseSelectionScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToHome, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToDecks, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEnvironments, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToAI, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.String, kotlin.Unit> onNavigateToExercise, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    com.example.study.ui.view.DeckViewModel deckViewModel, @org.jetbrains.annotations.NotNull()
    com.example.study.ui.view.FlashcardViewModel flashcardViewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ExerciseStatsCard(int totalDueCards, int totalDecks, kotlin.jvm.functions.Function0<kotlin.Unit> onStartMixedExercise, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchBar(java.lang.String query, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onQueryChange, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DeckExerciseList(java.util.List<com.example.study.data.Deck> decks, java.util.Map<com.example.study.data.Deck, ? extends androidx.compose.runtime.MutableState<java.lang.Integer>> deckStats, kotlin.jvm.functions.Function1<? super com.example.study.data.Deck, kotlin.Unit> onDeckClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DeckExerciseItem(com.example.study.data.Deck deck, int dueCount, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
}