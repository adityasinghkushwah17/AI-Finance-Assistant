package com.adityacodes.financebuddy.data

import com.adityacodes.financebuddy.OpenAIService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val openAIService: OpenAIService,
    private val financeAdviceDao: FinanceAdviceDao,
    private val chatMessageDao: ChatMessageDao,
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val auth: FirebaseAuth
) {
    suspend fun getChatResponse(messages: List<ChatMessage>): Result<String> {
        return try {
            val openAIMessages = messages.map {
                OpenAIMessage(
                    role = if (it.isUser) "user" else "assistant",
                    content = it.message
                )
            }

            val request = OpenAIRequest(
                model = "google/gemini-2.5-flash",
                messages = listOf(
                    OpenAIMessage(
                        role = "system",
                        content = "You are a personal finance assistant who gives very short, actionable advice in bullet points. Maximum 3-5 very precise bullet points."
                    ),
                    *openAIMessages.toTypedArray()
                )
            )


            val response = openAIService.getChatCompletion(request)

            Result.success(response.choices.first().message.content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getAllTransactionsForUser(userId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactionsForUser(userId)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
    suspend fun saveTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }


    private fun TransactionEntity.toModel(): Transaction = Transaction(
        id = id,
        amount = amount,
        tag = tag,
        note = note,
        timestamp = timestamp,
        userId = userId
    )

    private fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
        id = id,
        amount = amount,
        tag = tag,
        note = note,
        timestamp = timestamp,
        userId = userId
    )



    suspend fun insertGoal(goal: GoalEntity) = goalDao.insertGoal(goal)
    suspend fun getCurrentGoal(userId: String): GoalEntity? = goalDao.getCurrentGoalForUser(userId)
    fun getUserGoals(userId: String): Flow<List<GoalEntity>> {
        return goalDao.getUserGoals(userId)
    }
    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }
    fun getUserTotalSavings(userId: String): Flow<Double> {
        return transactionDao.getTotalSavingsFlow(userId)
    }


    suspend fun saveAdvice(advice: FinanceAdvice) {
        val userId = auth.currentUser?.uid ?: return
        financeAdviceDao.insertAdvice(
            FinanceAdviceEntity(
                id = advice.id,
                question = advice.question,
                advice = advice.advice,
                category = advice.category,
                timestamp = advice.timestamp,
                userId = userId
            )
        )
    }

    suspend fun getAllAdvice(): List<FinanceAdvice> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return financeAdviceDao.getAllAdviceForUser(userId).map { entity ->
            FinanceAdvice(
                id = entity.id,
                question = entity.question,
                advice = entity.advice,
                category = entity.category,
                timestamp = entity.timestamp,
                userId = entity.userId
            )
        }
    }

    suspend fun saveChatMessage(message: ChatMessage) {
        val userId = auth.currentUser?.uid ?: return
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                id = message.id,
                message = message.message,
                isUser = message.isUser,
                timestamp = message.timestamp,
                userId = userId
            )
        )
    }

    suspend fun getChatHistory(): List<ChatMessage> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return chatMessageDao.getAllMessagesForUser(userId).map { entity ->
            ChatMessage(
                id = entity.id,
                message = entity.message,
                isUser = entity.isUser,
                timestamp = entity.timestamp
            )
        }
    }
}