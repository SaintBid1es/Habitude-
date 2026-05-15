package com.example.habbitapp

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        android.os.Handler(android.os.Looper.getMainLooper()).post {
            setupDailyMigration()
        }

        applicationScope.launch {
            changeTransferAims()
        }

    }
    //TODO В ДОКУМЕНТАЦИЮ ИЗМЕНИТЬ ЖТОТ ФАЙЛ

    private fun setupDailyMigration() {

        if (!::workerFactory.isInitialized) {
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
    suspend fun changeTransferAims(){
        val viewModelAims : AimViewModel = AimViewModel()
        val date = LocalDate.now()
        coroutineScope {
            launch {
                viewModelAims.migrateUnfinishedTasks(date.toString())
            }
        }
    }
}