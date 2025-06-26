package com.adityacodes.financebuddy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID
import com.adityacodes.financebuddy.data.ChatMessage
import com.adityacodes.financebuddy.data.FinanceAdvice
import com.adityacodes.financebuddy.data.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _messages = mutableStateOf<List<ChatMessage>>(emptyList())
    val messages: State<List<ChatMessage>> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            try {
                _messages.value = repository.getChatHistory()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load chat history"
            }
        }
    }

    fun sendMessage(message: String) {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            message = message,
            isUser = true
        )

        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Save user message
                repository.saveChatMessage(userMessage)

                // Get AI response
                val result = repository.getChatResponse(_messages.value)

                result.onSuccess { response ->
                    val cleanedResponse = response
                        .replace("**", "")   
                        .replace("*", "•")
                    val aiMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        message = cleanedResponse,
                        isUser = false
                    )

                    _messages.value = _messages.value + aiMessage
                    repository.saveChatMessage(aiMessage)

                    // Save as advice if it's financial advice
                    if (isFinancialAdvice(message)) {
                        val advice = FinanceAdvice(
                            id = UUID.randomUUID().toString(),
                            question = message,
                            advice = response,
                            category = categorizeAdvice(message)
                        )
                        repository.saveAdvice(advice)
                    }
                }.onFailure { error ->
                    _errorMessage.value = "Failed to get response: ${error.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to send message: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun isFinancialAdvice(message: String): Boolean {
        val financeKeywords = listOf(
            "budget", "save", "invest", "money", "financial", "expense",
            "income", "debt", "credit", "loan", "retirement", "insurance"
        )
        return financeKeywords.any { message.lowercase().contains(it) }
    }

    private fun categorizeAdvice(message: String): String {
        return when {
            message.lowercase().contains("budget") -> "Budgeting"
            message.lowercase().contains("invest") -> "Investing"
            message.lowercase().contains("save") -> "Saving"
            message.lowercase().contains("debt") -> "Debt Management"
            message.lowercase().contains("retirement") -> "Retirement Planning"
            else -> "General"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}