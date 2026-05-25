package com.example.habbitapp.model.repository

import com.example.habbitapp.model.entity.Reminder
import com.example.habbitapp.model.entity.Task
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun observeAllReminder(): Flow<List<Reminder>>
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun findByIdReminderTask(id: Int): Reminder?
}