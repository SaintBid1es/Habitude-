package com.example.habbitapp

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbitapp.model.repository.AimRepository
import com.example.habbitapp.model.utils.DailySummaryScheduler
import com.example.habbitapp.model.utils.EndDayNotificationScheduler
import com.example.habbitapp.model.utils.TaskMigrationWorker
import com.example.habbitapp.viewmodel.AimViewModel
import dagger.hilt.android.HiltAndroidApp
import io.github.chouaibmo.rowkalendar.extensions.now
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    companion object {
        lateinit var appContext: Context
            private set
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

//    @Inject
//    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            //.setWorkerFactory(workerFactory)
            .build()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        android.os.Handler(
            android.os.Looper.getMainLooper()
        ).postDelayed({

            DailySummaryScheduler.schedule(this)
            EndDayNotificationScheduler.schedule(this)

        }, 2000)
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            setupDailyMigration()
        }


        // Запуск миграции задач
        applicationScope.launch {
            changeTransferAims()
        }


    }





    private fun setupDailyMigration() {
//        if (!::workerFactory.isInitialized) {
//            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
//                setupDailyMigration()
//            }, 100)
//            return
//        }

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

    suspend fun changeTransferAims() {
        val repository: AimRepository = AimViewModel.aimRepository()
        val date = LocalDate.now()
        coroutineScope {
            launch {
                repository.migrateUnfinishedTasks(date.toString())
            }
        }
    }
}