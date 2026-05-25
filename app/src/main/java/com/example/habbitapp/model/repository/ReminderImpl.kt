package com.example.habbitapp.model.repository

import com.example.habbitapp.model.dao.ReminderDao
import com.example.habbitapp.model.entity.Reminder

class ReminderImpl(
    private val dao: ReminderDao
):ReminderRepository {
    override suspend fun insertReminder(reminder: Reminder) {
        dao.insert(reminder)
    }

    override suspend fun updateReminder(reminder: Reminder) {
        dao.update(reminder)
    }

    override suspend fun findByIdReminderTask(id: Int): Reminder? {
        return dao.getReminderByIdTask(id)
    }
}