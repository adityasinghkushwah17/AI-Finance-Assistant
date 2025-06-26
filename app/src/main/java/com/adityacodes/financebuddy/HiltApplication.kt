package com.adityacodes.financebuddy

import android.app.Application
import android.content.res.Configuration
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FinanceBuddy : Application()