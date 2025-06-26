package com.adityacodes.financebuddy.DI

import android.content.Context
import androidx.room.Room
import com.adityacodes.financebuddy.OpenAIService
import com.adityacodes.financebuddy.data.AppDatabase
import com.adityacodes.financebuddy.data.ChatMessageDao
import com.adityacodes.financebuddy.data.FinanceAdviceDao
import com.adityacodes.financebuddy.data.GoalDao
import com.adityacodes.financebuddy.data.TransactionDao
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finance_ai_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFinanceAdviceDao(database: AppDatabase): FinanceAdviceDao {
        return database.financeAdviceDao()
    }

    @Provides
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val API_KEY =
        "API_KEY"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()

            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("X-Title", "FinanceBuddy")
                    .build()
                chain.proceed(newRequest)
            }

            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAIService(retrofit: Retrofit): OpenAIService {
        return retrofit.create(OpenAIService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides @Singleton
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()

    @Provides @Singleton
    fun provideContext(@ApplicationContext ctx: Context): Context = ctx
}