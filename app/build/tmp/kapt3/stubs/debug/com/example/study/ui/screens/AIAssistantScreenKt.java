package com.example.study.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001aJ\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0007\u001a:\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\u0003\u001a\u001a\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0003\u001a\u001c\u0010\u0014\u001a\u00020\u00012\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a\b\u0010\u0016\u001a\u00020\u0001H\u0003\u001a$\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u000b2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\rH\u0002\u00a8\u0006\u001a"}, d2 = {"AIAssistantScreen", "", "onNavigateToHome", "Lkotlin/Function0;", "onNavigateToDecks", "onNavigateToExercise", "onNavigateToEnvironments", "modifier", "Landroidx/compose/ui/Modifier;", "ChatInputArea", "inputText", "", "onInputChange", "Lkotlin/Function1;", "onSendMessage", "isLoading", "", "ChatMessageItem", "message", "Lcom/example/study/ui/screens/ChatMessage;", "QuickActionsRow", "onActionSelected", "TypingIndicator", "simulateAIResponse", "input", "onResponse", "app_debug"})
public final class AIAssistantScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AIAssistantScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToHome, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToDecks, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToExercise, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEnvironments, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChatMessageItem(com.example.study.ui.screens.ChatMessage message, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TypingIndicator() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QuickActionsRow(kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onActionSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChatInputArea(java.lang.String inputText, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onInputChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSendMessage, boolean isLoading) {
    }
    
    private static final void simulateAIResponse(java.lang.String input, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onResponse) {
    }
}