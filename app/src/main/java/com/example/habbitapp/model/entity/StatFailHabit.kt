package com.example.habbitapp.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habbitapp.model.database.Converters

@Entity(tableName = "statFailHabit")
@TypeConverters(Converters::class)
data class StatFailHabit(
    @PrimaryKey
    val id: Int = 1,
    val notPower: Int,
    val notTime: Int,
    val forgot: Int,
    val other: Int,
    val monthKey: String
    )


data class FailedHabitWithTask(
    val failedHabitId: Int,
    val taskId: Int,
    val date: String,
    val reasonSelected: Boolean,
    val taskName: String,
    val taskIcon: String
)