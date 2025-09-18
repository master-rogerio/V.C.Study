package com.example.study.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\rH\u0002J\u0010\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\nH\u0002J,\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J$\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u00112\u0006\u0010\u000f\u001a\u00020\nH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0018R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0019"}, d2 = {"Lcom/example/study/data/QuizApiRepository;", "", "()V", "geminiApiService", "Lcom/example/study/network/GeminiApiService;", "getGeminiApiService", "()Lcom/example/study/network/GeminiApiService;", "geminiApiService$delegate", "Lkotlin/Lazy;", "createPromptForFlashcards", "", "topic", "type", "Lcom/example/study/data/FlashcardType;", "createPromptForQuiz", "theme", "generateFlashcards", "Lkotlin/Result;", "Lcom/example/study/network/FlashcardListResponse;", "generateFlashcards-0E7RQCE", "(Ljava/lang/String;Lcom/example/study/data/FlashcardType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "generateQuizQuestion", "Lcom/example/study/network/ApiQuizQuestion;", "generateQuizQuestion-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class QuizApiRepository {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy geminiApiService$delegate = null;
    
    public QuizApiRepository() {
        super();
    }
    
    private final com.example.study.network.GeminiApiService getGeminiApiService() {
        return null;
    }
    
    private final java.lang.String createPromptForQuiz(java.lang.String theme) {
        return null;
    }
    
    private final java.lang.String createPromptForFlashcards(java.lang.String topic, com.example.study.data.FlashcardType type) {
        return null;
    }
}