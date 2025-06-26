package com.adityacodes.financebuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "goal_channel_id"
    private const val CHANNEL_NAME = "Goal Progress"
    private const val NOTIFICATION_ID = 101

    fun showGoalReachedNotification(
        context: Context,
        goalDescription: String,
        targetAmount: Double
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background) // add an icon in res/drawable
            .setContentTitle("🎯 Goal reached!")
            .setContentText("Your goal \"$goalDescription\" of ₹$targetAmount is completed!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
