package com.adityacodes.financebuddy.data

import java.util.UUID

data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val isUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class FinanceAdvice(
    val id: String = "",
    val question: String = "",
    val advice: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)

data class OpenAIRequest(
    val model: String = "google/gemini-2.5-flash",
    val messages: List<OpenAIMessage>,
    val max_tokens: Int = 100,
    val temperature: Double = 0.3
)

data class OpenAIMessage(
    val role: String,
    val content: String
)

data class OpenAIResponse(
    val choices: List<OpenAIChoice>
)

data class OpenAIChoice(
    val message: OpenAIMessage
)
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val tag: String,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String
)