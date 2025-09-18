package com.example.study.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\n"}, d2 = {"Lcom/example/study/network/GeminiApiService;", "", "generateContent", "Lretrofit2/Response;", "Lcom/example/study/network/GeminiResponse;", "apiKey", "", "requestBody", "Lcom/example/study/network/GenerateQuestionRequest;", "(Ljava/lang/String;Lcom/example/study/network/GenerateQuestionRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface GeminiApiService {
    
    @retrofit2.http.POST(value = "v1beta/models/gemini-1.5-flash-latest:generateContent")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object generateContent(@retrofit2.http.Query(value = "key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.example.study.network.GenerateQuestionRequest requestBody, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.study.network.GeminiResponse>> $completion);
}