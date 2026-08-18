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

class EndDayNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "end_day_channel"
        const val NOTIFICATION_ID = 999
        private const val TAG = "EndDay"
        @RequiresApi(Build.VERSION_CODES.O)
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action != "END_DAY") {
            return
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "POST_NOTIFICATIONS permission denied")

                EndDayNotificationScheduler.schedule(context)

                return
            }
        }

        goAsync().also { pendingResult ->

            kotlinx.coroutines.CoroutineScope(
                kotlinx.coroutines.Dispatchers.IO
            ).launch {

                try {

                    showEndDay(context)

                } catch (e: Exception) {

                    Log.e(TAG, "Error showing daily summary", e)

                } finally {
                    EndDayNotificationScheduler.schedule(context)
                    pendingResult.finish()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun showEndDay(context: Context) {
        try {
            Log.d(TAG, "endDay called")

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Конец дня",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Ежедневная сводка окончания дня об привычках на сегодня, если не выполнили"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created")
            }


            val stats = getTodayStats(context)

            val summaryText = buildString {

                if (stats.tasks > 0) {
                    append("📝 Целей на сегодня: ${stats.tasks}\n")
                }
                if (stats.habits > 0) {
                    append("🔄 Привычек на сегодня: ${stats.habits}\n")
                }


            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("До конца дня осталось 4 часа,ты еще не сделал")
                .setContentText("У вас ${stats.tasks + stats.habits} осталось на сегодня")
                .setStyle(NotificationCompat.BigTextStyle().bigText(summaryText))
                .setSmallIcon(R.drawable.habit_ic)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 200, 500))
                .build()
            if (stats.tasks != 0 || stats.habits != 0) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
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
            val allTasks = dao.getAllTasksBlockingForNotification()
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