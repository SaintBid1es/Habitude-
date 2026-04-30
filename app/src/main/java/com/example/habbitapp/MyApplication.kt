package com.example.habbitapp

import android.app.Application
import android.content.Context

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbitapp.model.utils.TaskMigrationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.Configuration
@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    companion object {
        lateinit var appContext: Context
            private set
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        setupDailyMigration()
    }

    private fun setupDailyMigration() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val migrationRequest = PeriodicWorkRequestBuilder<TaskMigrationWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyTaskMigration",
            ExistingPeriodicWorkPolicy.KEEP,
            migrationRequest
        )
    }
}
