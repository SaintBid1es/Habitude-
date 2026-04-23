package com.example.habbitapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import java.util.Calendar

class AlarmReceiver() : BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?,) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                android.util.Log.e("AlarmReceiver", "No notification permission")
                return
            }
        }
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val habitName = intent?.getStringExtra("habit_name") ?: "Напоминание"
        val habitDescription = intent?.getStringExtra("habit_description") ?: "Время выполнить привычку!"
        val habitId = intent?.getIntExtra("habit_id", -1) ?: -1
        val channelId = "habit_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Alarm", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(habitName)
            .setContentText(habitDescription)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(habitId, notification)
    }
}

fun setTimeNotification( context:Context,habitName: String,
                         habitDescription: String,
                         hour: Int,
                         minute: Int,
                         daysOfWeek: List<Boolean>,
                         habitId:Int){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    daysOfWeek.forEachIndexed { index, isSelected ->
        if (isSelected) {
            val calendarDay = when (index) {
                0 -> Calendar.MONDAY
                1 -> Calendar.TUESDAY
                2 -> Calendar.WEDNESDAY
                3 -> Calendar.THURSDAY
                4 -> Calendar.FRIDAY
                5 -> Calendar.SATURDAY
                6 -> Calendar.SUNDAY
                else -> Calendar.MONDAY
            }

            scheduleWeeklyReminder(
                context = context,
                habitId = habitId,
                habitName = habitName,
                habitDescription = habitDescription,
                hour = hour,
                minute = minute,
                dayOfWeek = calendarDay,
                alarmManager = alarmManager
            )
        }
    }
}

private fun scheduleWeeklyReminder(
    context: Context,
    habitId: Int,
    habitName: String,
    habitDescription: String,
    hour: Int,
    minute: Int,
    dayOfWeek: Int,
    alarmManager: AlarmManager
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_WEEK, dayOfWeek)


        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_YEAR, 7)
        }
    }

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("habit_id", habitId)
        putExtra("habit_name", habitName)
        putExtra("habit_description", habitDescription)
        putExtra("hour", hour)
        putExtra("minute", minute)
    }


    val requestCode = "$habitId$dayOfWeek".hashCode()

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    android.util.Log.d("AlarmManager", "Scheduled reminder for $habitName on day $dayOfWeek at $hour:$minute")
}