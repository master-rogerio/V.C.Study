package com.example.study.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aT\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a.\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a@\u0010\u0012\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a8\u0010\u0013\u001a\u00020\u00012\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00010\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a$\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00010\u0018H\u0003\u001a*\u0010\u001c\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0003\u001a\u0010\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020 H\u0003\u001a\u0016\u0010!\u001a\u00020\u00012\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u00a8\u0006\""}, d2 = {"HomeScreen", "", "onNavigateToDecks", "Lkotlin/Function0;", "onNavigateToExercise", "onNavigateToEnvironments", "onNavigateToAI", "modifier", "Landroidx/compose/ui/Modifier;", "viewModel", "Lcom/example/study/ui/view/HomeViewModel;", "QuickActionCard", "title", "", "subtitle", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "onClick", "QuickActionsSection", "RecentActivitySection", "recentDecks", "", "Lcom/example/study/data/Deck;", "onDeckClick", "Lkotlin/Function1;", "onStartStudyClick", "RecentDeckCard", "deck", "StatCard", "value", "StudyStatsSection", "uiState", "Lcom/example/study/ui/view/HomeUiState;", "WelcomeHeroSection", "app_debug"})
public final class HomeScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToDecks, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToExercise, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEnvironments, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToAI, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    com.example.study.ui.view.HomeViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void WelcomeHeroSection(kotlin.jvm.functions.Function0<kotlin.Unit> onStartStudyClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StudyStatsSection(com.example.study.ui.view.HomeUiState uiState) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StatCard(java.lang.String title, java.lang.String value, androidx.compose.ui.graphics.vector.ImageVector icon, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QuickActionsSection(kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToDecks, kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToExercise, kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEnvironments, kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToAI) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QuickActionCard(java.lang.String title, java.lang.String subtitle, androidx.compose.ui.graphics.vector.ImageVector icon, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RecentActivitySection(java.util.List<com.example.study.data.Deck> recentDecks, kotlin.jvm.functions.Function1<? super com.example.study.data.Deck, kotlin.Unit> onDeckClick, kotlin.jvm.functions.Function0<kotlin.Unit> onStartStudyClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RecentDeckCard(com.example.study.data.Deck deck, kotlin.jvm.functions.Function1<? super com.example.study.data.Deck, kotlin.Unit> onDeckClick) {
    }
}