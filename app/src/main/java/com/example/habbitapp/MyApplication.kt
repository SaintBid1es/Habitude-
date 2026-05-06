package com.example.habbitapp

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbitapp.model.utils.TaskMigrationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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

        // Запускаем инициализацию WorkManager после того, как Hilt завершит инъекцию
        // Используем postDelayed, чтобы дать Hilt время на внедрение зависимостей
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            setupDailyMigration()
        }
    }
    //TODO В ДОКУМЕНТАЦИЮ ИЗМЕНИТЬ ЖТОТ ФАЙЛ

    private fun setupDailyMigration() {
        // Проверяем, инициализирован ли workerFactory
        if (!::workerFactory.isInitialized) {
            // Если ещё нет, ждём ещё немного
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                setupDailyMigration()
            }, 100)
            return
        }

        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}