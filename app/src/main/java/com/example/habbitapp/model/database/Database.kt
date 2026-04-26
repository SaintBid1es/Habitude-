package com.example.habbitapp.model.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.habbitapp.model.dao.AimsDao
import com.example.habbitapp.model.dao.ReminderDao
import com.example.habbitapp.model.dao.TaskDao
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.model.entity.Reminder
import com.example.habbitapp.model.entity.Task

@Database(entities = [Task::class, Reminder::class, Aims::class], version = 9, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun aimsDao(): AimsDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                ) .fallbackToDestructiveMigration() // удаляет все данные при изменении схемы бд
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}