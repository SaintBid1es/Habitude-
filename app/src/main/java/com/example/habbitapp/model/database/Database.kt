package com.example.habbitapp.model.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.habbitapp.model.dao.AimsDao
import com.example.habbitapp.model.dao.FailedHabitDao
import com.example.habbitapp.model.dao.ReminderDao
import com.example.habbitapp.model.dao.StatFailHabitDao
import com.example.habbitapp.model.dao.TaskDao
import com.example.habbitapp.model.database.migration.MIGRATION_13_14
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.model.entity.FailedHabit
import com.example.habbitapp.model.entity.Reminder
import com.example.habbitapp.model.entity.StatFailHabit
import com.example.habbitapp.model.entity.Task

@Database(version = 14,
    entities = [Task::class, Aims::class, Reminder::class, StatFailHabit::class, FailedHabit::class],
    exportSchema = true


)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun statFailHabitDao(): StatFailHabitDao
    abstract fun aimsDao(): AimsDao
    abstract fun reminderDao(): ReminderDao
    abstract fun failedHabitDao(): FailedHabitDao
    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                ) //.fallbackToDestructiveMigration() // // удаляет все данные при изменении схемы бд
                    .addMigrations(MIGRATION_13_14)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}