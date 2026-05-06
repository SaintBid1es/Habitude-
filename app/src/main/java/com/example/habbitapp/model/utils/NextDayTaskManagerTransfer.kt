package com.example.habbitapp.model.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habbitapp.viewmodel.AimViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class TaskMigrationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val  viewModel: AimViewModel
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())
            viewModel.migrateUnfinishedTasks(today)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
