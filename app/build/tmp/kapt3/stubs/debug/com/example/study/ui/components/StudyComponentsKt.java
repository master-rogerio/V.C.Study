package com.example.study.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000l\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001a0\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u001c\u0010\u0004\u001a\u0018\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005\u00a2\u0006\u0002\b\u0007\u00a2\u0006\u0002\b\bH\u0007\u001a.\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001aR\u0010\r\u001a\u00020\u00012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00112\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007\u001aV\u0010\u0019\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0010\b\u0002\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000f2\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\u001c\u0010\u0004\u001a\u0018\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020\u00010\u0005\u00a2\u0006\u0002\b\u0007\u00a2\u0006\u0002\b\bH\u0007\u001aN\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010 \u001a\u00020\u00112\u0010\b\u0002\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000f2\n\b\u0002\u0010!\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u0015H\u0007\u001aH\u0010#\u001a\u00020\u00012\u0006\u0010$\u001a\u00020\u00132\u0006\u0010%\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010&\u001a\u0004\u0018\u00010\u00132\u0010\b\u0002\u0010\'\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000fH\u0007\u001aL\u0010(\u001a\u00020\u00012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0014\u001a\u00020\u00152\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u00132\b\b\u0002\u0010*\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0007\u001a.\u0010+\u001a\u00020\u00012\u0006\u0010,\u001a\u00020-2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010.\u001a\u00020\u00112\b\b\u0002\u0010/\u001a\u00020\u0011H\u0007\u001a0\u00100\u001a\u00020\u00012\b\b\u0002\u00101\u001a\u00020\u00112\u001c\u0010\u0004\u001a\u0018\u0012\u0004\u0012\u000202\u0012\u0004\u0012\u00020\u00010\u0005\u00a2\u0006\u0002\b\u0007\u00a2\u0006\u0002\b\bH\u0007\u00a8\u00063"}, d2 = {"GradientBackground", "", "modifier", "Landroidx/compose/ui/Modifier;", "content", "Lkotlin/Function1;", "Landroidx/compose/foundation/layout/BoxScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "StudyBottomNavigation", "selectedItem", "", "onItemSelected", "StudyButton", "onClick", "Lkotlin/Function0;", "enabled", "", "text", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "isLoading", "variant", "Lcom/example/study/ui/components/ButtonVariant;", "StudyCard", "elevation", "Landroidx/compose/material3/CardElevation;", "colors", "Landroidx/compose/material3/CardColors;", "Landroidx/compose/foundation/layout/ColumnScope;", "StudyChip", "selected", "leadingIcon", "trailingIcon", "StudyEmptyState", "title", "subtitle", "actionText", "onActionClick", "StudyFAB", "contentDescription", "expanded", "StudyProgressBar", "progress", "", "showPercentage", "animated", "StudyScaleInAnimation", "visible", "Landroidx/compose/animation/AnimatedVisibilityScope;", "app_debug"})
public final class StudyComponentsKt {
    
    @androidx.compose.runtime.Composable()
    public static final void StudyCard(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.material3.CardElevation elevation, @org.jetbrains.annotations.NotNull()
    androidx.compose.material3.CardColors colors, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.ColumnScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyButton(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, boolean enabled, @org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.Nullable()
    androidx.compose.ui.graphics.vector.ImageVector icon, boolean isLoading, @org.jetbrains.annotations.NotNull()
    com.example.study.ui.components.ButtonVariant variant) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyFAB(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.Nullable()
    java.lang.String contentDescription, boolean expanded, @org.jetbrains.annotations.Nullable()
    java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyChip(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, boolean selected, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.Nullable()
    androidx.compose.ui.graphics.vector.ImageVector leadingIcon, @org.jetbrains.annotations.Nullable()
    androidx.compose.ui.graphics.vector.ImageVector trailingIcon) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyProgressBar(float progress, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, boolean showPercentage, boolean animated) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyEmptyState(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String subtitle, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    java.lang.String actionText, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onActionClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GradientBackground(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.BoxScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyBottomNavigation(int selectedItem, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onItemSelected, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StudyScaleInAnimation(boolean visible, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.animation.AnimatedVisibilityScope, kotlin.Unit> content) {
    }
}