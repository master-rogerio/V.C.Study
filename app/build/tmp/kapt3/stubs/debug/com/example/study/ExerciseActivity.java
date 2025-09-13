package com.example.study;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\nH\u0002J\b\u0010\u0016\u001a\u00020\u0013H\u0002J\u0012\u0010\u0017\u001a\u00020\u00132\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\u0010\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001f\u001a\u00020 H\u0016J\b\u0010!\u001a\u00020\u001bH\u0016J\u0010\u0010\"\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000eH\u0002J\b\u0010#\u001a\u00020\u0013H\u0002J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000eH\u0002J\u0010\u0010%\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000eH\u0002J\u0010\u0010&\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000eH\u0002J\b\u0010\'\u001a\u00020\u0013H\u0002J\b\u0010(\u001a\u00020\u0013H\u0002J\b\u0010)\u001a\u00020\u0013H\u0002J\u0010\u0010*\u001a\u00020\u00132\u0006\u0010+\u001a\u00020\nH\u0002J\b\u0010,\u001a\u00020\u0013H\u0002J\u0010\u0010-\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000eH\u0002J\b\u0010.\u001a\u00020\u0013H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006/"}, d2 = {"Lcom/example/study/ExerciseActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/study/databinding/ActivityExerciseBinding;", "correctAnswers", "", "currentDeckId", "", "currentDeckName", "", "currentIndex", "flashcards", "", "Lcom/example/study/data/Flashcard;", "viewModel", "Lcom/example/study/ui/FlashcardViewModel;", "wrongAnswers", "checkAnswer", "", "flashcard", "userAnswer", "moveToNextFlashcard", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "onSupportNavigateUp", "setupClozeLayout", "setupExercise", "setupFrontBackLayout", "setupMultipleChoiceLayout", "setupTextInputLayout", "showCorrectFeedback", "showCurrentFlashcard", "showEmptyState", "showError", "message", "showExerciseResults", "showWrongFeedback", "shuffleFlashcards", "app_debug"})
public final class ExerciseActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.study.databinding.ActivityExerciseBinding binding;
    private com.example.study.ui.FlashcardViewModel viewModel;
    private long currentDeckId = -1L;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentDeckName = "";
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.example.study.data.Flashcard> flashcards;
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    
    public ExerciseActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public boolean onCreateOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    private final void shuffleFlashcards() {
    }
    
    private final void setupExercise() {
    }
    
    private final void showEmptyState() {
    }
    
    private final void showCurrentFlashcard() {
    }
    
    private final void setupFrontBackLayout(com.example.study.data.Flashcard flashcard) {
    }
    
    private final void setupClozeLayout(com.example.study.data.Flashcard flashcard) {
    }
    
    private final void setupTextInputLayout(com.example.study.data.Flashcard flashcard) {
    }
    
    private final void setupMultipleChoiceLayout(com.example.study.data.Flashcard flashcard) {
    }
    
    private final void checkAnswer(com.example.study.data.Flashcard flashcard, java.lang.String userAnswer) {
    }
    
    private final void showCorrectFeedback() {
    }
    
    private final void showWrongFeedback(com.example.study.data.Flashcard flashcard) {
    }
    
    private final void moveToNextFlashcard() {
    }
    
    private final void showExerciseResults() {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
}