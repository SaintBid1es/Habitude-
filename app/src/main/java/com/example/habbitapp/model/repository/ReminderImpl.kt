package com.example.habbitapp.model.repository

import com.example.habbitapp.model.dao.ReminderDao
import com.example.habbitapp.model.entity.Reminder
import com.example.habbitapp.model.entity.Task
import kotlinx.coroutines.flow.Flow

class ReminderImpl(
    private val dao: ReminderDao
):ReminderRepository {
    override fun observeAllReminder(): Flow<List<Reminder>> = dao.getAllReminder()
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