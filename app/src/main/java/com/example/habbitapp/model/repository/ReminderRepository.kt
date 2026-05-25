package com.example.habbitapp.model.repository

import com.example.habbitapp.model.entity.Reminder

interface ReminderRepository {
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun findByIdReminderTask(id: Int): Reminder?
}