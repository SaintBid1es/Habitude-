package com.example.habbitapp.model.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habbitapp.model.database.ItemDatabase

class CheckMissedHabitsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        return try {

            val database =
                ItemDatabase.getInstance(
                    applicationContext
                )

            val tasks =
                database
                    .taskDao()
                    .getAllTasksBlocking()

            FailedHabitManager.checkMissedHabits(
                applicationContext,
                tasks
            )

            Result.success()

        } catch (e: Exception) {

            Log.e(
                "CheckMissedHabits",
                "Error",
                e
            )

            Result.retry()
        }
    }
}