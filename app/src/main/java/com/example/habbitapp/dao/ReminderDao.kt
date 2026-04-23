package com.example.habbitapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habbitapp.entity.Reminder
import com.example.habbitapp.entity.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun getAllReminder(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE habitId = :id")
    suspend fun getReminderByIdTask(id: Int): Reminder

    @Query("Delete from reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Int)
}