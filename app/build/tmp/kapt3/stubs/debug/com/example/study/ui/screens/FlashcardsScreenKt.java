package com.example.study.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a$\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u0010\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007H\u0003\u001a\u0010\u0010\b\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007H\u0003\u001a6\u0010\t\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\f\u001a\u00020\rH\u0003\u001aH\u0010\u000e\u001a\u00020\u00012\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00102\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u00112\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\f\u001a\u00020\rH\u0003\u001aH\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u0019\u001a\u00020\u001aH\u0007\u001a.\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u00162\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\f\u001a\u00020\rH\u0003\u00a8\u0006\u001e"}, d2 = {"DeleteFlashcardDialog", "", "onConfirm", "Lkotlin/Function0;", "onDismiss", "FlashcardBack", "flashcard", "Lcom/example/study/data/Flashcard;", "FlashcardFront", "FlashcardItem", "onEditClick", "onDeleteClick", "modifier", "Landroidx/compose/ui/Modifier;", "FlashcardsList", "flashcards", "", "Lkotlin/Function1;", "FlashcardsScreen", "deckId", "", "deckName", "", "onNavigateBack", "onNavigateToExercise", "viewModel", "Lcom/example/study/ui/view/FlashcardViewModel;", "SearchBar", "query", "onQueryChange", "app_debug"})
public final class FlashcardsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void FlashcardsScreen(long deckId, @org.jetbrains.annotations.NotNull()
    java.lang.String deckName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToExercise, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    com.example.study.ui.view.FlashcardViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchBar(java.lang.String query, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onQueryChange, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FlashcardsList(java.util.List<com.example.study.data.Flashcard> flashcards, kotlin.jvm.functions.Function1<? super com.example.study.data.Flashcard, kotlin.Unit> onEditClick, kotlin.jvm.functions.Function1<? super com.example.study.data.Flashcard, kotlin.Unit> onDeleteClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FlashcardItem(com.example.study.data.Flashcard flashcard, kotlin.jvm.functions.Function0<kotlin.Unit> onEditClick, kotlin.jvm.functions.Function0<kotlin.Unit> onDeleteClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FlashcardFront(com.example.study.data.Flashcard flashcard) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FlashcardBack(com.example.study.data.Flashcard flashcard) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DeleteFlashcardDialog(kotlin.jvm.functions.Function0<kotlin.Unit> onConfirm, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}