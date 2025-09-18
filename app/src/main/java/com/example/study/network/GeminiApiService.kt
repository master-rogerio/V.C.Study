package com.example.study.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    suspend fun generateContent(@Query("key") apiKey: String, @Body requestBody: GenerateQuestionRequest): Response<GeminiResponse>
}