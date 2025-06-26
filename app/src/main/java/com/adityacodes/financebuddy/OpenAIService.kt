package com.adityacodes.financebuddy

import com.adityacodes.financebuddy.data.OpenAIRequest
import com.adityacodes.financebuddy.data.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Body request: OpenAIRequest
    ): OpenAIResponse
}