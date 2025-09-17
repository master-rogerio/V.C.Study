package com.example.study;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\n\u001a\u00020\u000bH\u0002J\u0010\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\b\u0010\u000f\u001a\u00020\u000bH\u0002J\b\u0010\u0010\u001a\u00020\u000bH\u0002J\u0012\u0010\u0011\u001a\u00020\u000b2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u000bH\u0002J\u0010\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/study/AIQuizActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/study/databinding/ActivityAiQuizBinding;", "currentCorrectAnswer", "", "quizTheme", "viewModel", "Lcom/example/study/ui/view/FlashcardViewModel;", "checkAnswer", "", "displayQuestion", "question", "Lcom/example/study/network/ApiQuizQuestion;", "fetchNewQuestion", "observeViewModel", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onSupportNavigateUp", "", "setupClickListeners", "showErrorDialog", "errorMessage", "app_debug"})
public final class AIQuizActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.study.databinding.ActivityAiQuizBinding binding;
    private com.example.study.ui.view.FlashcardViewModel viewModel;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String quizTheme = "";
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentCorrectAnswer;
    
    public AIQuizActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void fetchNewQuestion() {
    }
    
    private final void observeViewModel() {
    }
    
    private final void displayQuestion(com.example.study.network.ApiQuizQuestion question) {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void checkAnswer() {
    }
    
    private final void showErrorDialog(java.lang.String errorMessage) {
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
}