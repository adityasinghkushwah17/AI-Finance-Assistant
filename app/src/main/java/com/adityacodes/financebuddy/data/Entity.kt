package com.adityacodes.financebuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finance_advice")
data class FinanceAdviceEntity(
    @PrimaryKey val id: String,
    val question: String,
    val advice: String,
    val category: String,
    val timestamp: Long,
    val userId: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val message: String,
    val isUser: Boolean,
    val timestamp: Long,
    val userId: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val tag: String,
    val note: String?,
    val timestamp: Long,
    val userId: String
)
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val targetAmount: Double,
    val targetDateMillis: Long,
    val userId: String
)