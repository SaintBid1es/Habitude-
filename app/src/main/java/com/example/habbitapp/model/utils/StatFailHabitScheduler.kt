package com.example.habbitapp.model.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.Calendar

object StatFailHabitScheduler {

    private const val TAG =
        "StatFailHabitScheduler"

    private const val REQUEST_CODE =
        10001

    fun schedule(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {

                Log.e(
                    TAG,
                    "Exact alarm permission is not granted"
                )

                return
            }
        }

        val calendar =
            Calendar.getInstance().apply {

                /*
                 * Первый день следующего месяца.
                 */
                add(Calendar.MONTH, 1)

                set(
                    Calendar.DAY_OF_MONTH,
                    1
                )

                set(
                    Calendar.HOUR_OF_DAY,
                    6
                )

                set(
                    Calendar.MINUTE,
                    0
                )

                set(
                    Calendar.SECOND,
                    0
                )

                set(
                    Calendar.MILLISECOND,
                    0
                )
            }

        val intent = Intent(
            context,
            StatFailHabitReceiver::class.java
        ).apply {

            action = "STAT_FAIL_HABIT"
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d(
            TAG,
            "Next monthly stat scheduled: " +
                    calendar.time
        )
    }
}