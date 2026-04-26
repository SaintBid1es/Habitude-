package com.example.habbitapp.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habbitapp.model.database.Converters

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
    val habitId: Int,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Boolean>? = null
)