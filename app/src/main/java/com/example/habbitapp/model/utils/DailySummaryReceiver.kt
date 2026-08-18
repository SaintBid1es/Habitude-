package com.example.habbitapp.model.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.habbitapp.R
import com.example.habbitapp.model.database.ItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailySummaryReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "daily_summary_channel"
        const val NOTIFICATION_ID = 999
        private const val TAG = "DailySummary"
        @RequiresApi(Build.VERSION_CODES.O)
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action != "DAILY_SUMMARY") {
            return
        }

        Log.d(TAG, "Daily summary alarm received")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "POST_NOTIFICATIONS permission denied")

                DailySummaryScheduler.schedule(context)

                return
            }
        }

        goAsync().also { pendingResult ->

            kotlinx.coroutines.CoroutineScope(
                kotlinx.coroutines.Dispatchers.IO
            ).launch {

                try {

                    showDailySummary(context)

                } catch (e: Exception) {

                    Log.e(TAG, "Error showing daily summary", e)

                } finally {

                    // Ставим следующий запуск на завтра 06:00
                    DailySummaryScheduler.schedule(context)

                    pendingResult.finish()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun showDailySummary(context: Context) {
        try {
            Log.d(TAG, "showDailySummary called")

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Ежедневная сводка",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Ежедневная сводка о задачах, привычках и целях"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created")
            }

            // Получаем статистику на сегодня
            val stats = getTodayStats(context)
            Log.d(TAG, "Stats for today: tasks=${stats.tasks}, habits=${stats.habits}")

            val summaryText = buildString {
                append("📊 Доброе утро! Ваша сводка на сегодня:\n\n")

                if (stats.tasks > 0) {
                    append("📝 Целей на сегодня: ${stats.tasks}\n")
                }
                if (stats.habits > 0) {
                    append("🔄 Привычек на сегодня: ${stats.habits}\n")
                }

                if (stats.tasks == 0 && stats.habits == 0) {
                    append("😊 У вас нет запланированных дел на сегодня.\nНаслаждайтесь свободным днем!")
                } else {
                    append("\n💪 Удачи в выполнении!\nХорошего дня! 🌟")
                }
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("🌅 Доброе утро! Ваша сводка на сегодня")
                .setContentText("У вас ${stats.tasks + stats.habits} дел на сегодня")
                .setStyle(NotificationCompat.BigTextStyle().bigText(summaryText))
                .setSmallIcon(R.drawable.habit_ic)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 200, 500))
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "Notification shown successfully!")

        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getTodayStats(context: Context): DailyStats {
        return try {
            val today = LocalDate.now().format(DATE_FORMATTER)
            Log.d(TAG, "Today date: $today")

            DailyStats(
                tasks = getTasksCountToday(context),
                habits = getHabitsCountToday(context),

            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting stats", e)
            DailyStats(0, 0)
        }
    }

    /**
     * Получить количество задач на сегодня
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getTasksCountToday(context: Context): Int {
        return try {
            val dao = ItemDatabase.getInstance(context).aimsDao()
            val today = LocalDate.now().toString()
            val allTasks = dao.getActiveCountForDate(today)

            Log.d(TAG, "Tasks for today: $allTasks")
            allTasks
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks count", e)
            0
        }
    }

    /**
     * Получить количество привычек на сегодня
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getHabitsCountToday(context: Context): Int {
        return try {
            val dao = ItemDatabase.getInstance(context).taskDao()
            // Аналогично задачам - привычки это те же Task
            val allTasks = dao.getAllTasksBlocking()
            val today = LocalDate.now().dayOfWeek.value - 1
            val todayHabits = allTasks.filter { it.days.getOrNull(today) == true }
            val count = todayHabits.size
            Log.d(TAG, "Habits for today: $count")
            count
        } catch (e: Exception) {
            Log.e(TAG, "Error getting habits count", e)
            0
        }
    }

    /**
     * Получить количество целей на сегодня
     */


    data class DailyStats(
        val tasks: Int,
        val habits: Int,
    )
}