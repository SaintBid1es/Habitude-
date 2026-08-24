package com.example.habbitapp.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "failedHabit",
    indices = [
        Index(
            value = ["taskId", "date"],
            unique = true
        )
    ]
)
data class FailedHabit(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val taskId: Int,

    /**
     * Дата, когда привычка была пропущена.
     * Например: 2026-08-23
     */
    val date: String,

    /**
     * false = пользователь ещё не указал причину
     * true = причина уже указана
     */
    val reasonSelected: Boolean = false
)