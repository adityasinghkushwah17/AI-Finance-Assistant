package com.adityacodes.financebuddy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Database(
    entities = [FinanceAdviceEntity::class, ChatMessageEntity::class, TransactionEntity::class, GoalEntity::class],
    version =3 ,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeAdviceDao(): FinanceAdviceDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao

}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}