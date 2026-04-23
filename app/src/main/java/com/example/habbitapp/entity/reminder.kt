package com.example.habbitapp.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habbitapp.Converters

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitId: Int,  // внешний ключ к Habit
    val hour: Int,     // 0-23
    val minute: Int,   // 0-59
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Boolean>? = null  // если нужно разное время в разные дни
)