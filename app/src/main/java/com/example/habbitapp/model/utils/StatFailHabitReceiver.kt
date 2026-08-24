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
import com.example.habbitapp.model.entity.StatFailHabit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatFailHabitReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "stat_fail_channel"
        const val NOTIFICATION_ID = 999
        private const val TAG = "StatFailHabit"
        @RequiresApi(Build.VERSION_CODES.O)
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action != "STAT_FAIL_HABIT") {
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

                DailySummaryScheduler.schedule(context)

                return
            }
        }

        goAsync().also { pendingResult ->

            kotlinx.coroutines.CoroutineScope(
                kotlinx.coroutines.Dispatchers.IO
            ).launch {

                try {

                    statFailHabit(context)

                } catch (e: Exception) {


                } finally {

                    StatFailHabitScheduler.schedule(context)

                    pendingResult.finish()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun statFailHabit(context: Context) {
        try {

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Ежемесячная сводка",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Ежемесячная сводка о невыполненых привычках и целях"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created")
            }

            val stats = getTodayStats(context)

            val summaryText = buildString {
                val count =
                    stats.forgot +
                            stats.other +
                            stats.notTime +
                            stats.notPower

                if (count > 0) {

                    val notPowerPercent =
                        stats.notPower * 100 / count

                    val notTimePercent =
                        stats.notTime * 100 / count

                    val forgotPercent =
                        stats.forgot * 100 / count

                    val otherPercent =
                        stats.other * 100 / count

                    append(
                        "$notPowerPercent% - усталость\n"
                    )

                    append(
                        "$notTimePercent% - не было времени\n"
                    )

                    append(
                        "$forgotPercent% - забывчивость\n"
                    )

                    append(
                        "$otherPercent% - другие причины\n"
                    )

                } else {

                    append(
                        "Вы молодцы — " +
                                "не пропустили за месяц ни одной привычки!\n" +
                                "Наслаждайтесь свободным днем!"
                    )
                }
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("📊 Ежемесячная сводка о невыполненых целях и привычках")
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
    private suspend fun getTodayStats(
        context: Context
    ): StatFailHabit {

        return try {

            val dao = ItemDatabase
                .getInstance(context)
                .statFailHabitDao()

            dao.getStatFailHabit()
                ?: StatFailHabit(
                    id = 1,
                    notPower = 0,
                    notTime = 0,
                    forgot = 0,
                    other = 0,
                    monthKey = java.time.YearMonth
                        .now()
                        .toString()
                )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error getting stats",
                e
            )

            StatFailHabit(
                id = 1,
                notPower = 0,
                notTime = 0,
                forgot = 0,
                other = 0,
                monthKey = java.time.YearMonth
                    .now()
                    .toString()
            )
        }
    }






}