package com.adityacodes.financebuddy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceAdviceDao {
    @Query("SELECT * FROM finance_advice WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllAdviceForUser(userId: String): List<FinanceAdviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvice(advice: FinanceAdviceEntity)

    @Query("DELETE FROM finance_advice WHERE id = :id")
    suspend fun deleteAdvice(id: String)

    @Query("SELECT * FROM finance_advice WHERE category = :category AND userId = :userId ORDER BY timestamp DESC")
    suspend fun getAdviceByCategory(category: String, userId: String): List<FinanceAdviceEntity>
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getAllMessagesForUser(userId: String): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun clearMessagesForUser(userId: String)
}

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
     fun getAllTransactionsForUser(userId: String): Flow<List<TransactionEntity>>
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE userId = :userId")
    fun getTotalSavingsFlow(userId: String): Flow<Double>
}

@Dao
interface GoalDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE userId = :userId LIMIT 1")
    suspend fun getCurrentGoalForUser(userId: String): GoalEntity?

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE userId = :userId")
    fun getUserGoals(userId: String): Flow<List<GoalEntity>>
}