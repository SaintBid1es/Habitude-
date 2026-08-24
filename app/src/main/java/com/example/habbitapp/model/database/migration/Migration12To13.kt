package com.example.habbitapp.model.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_13_14 = object : Migration(13, 14) {

    override fun migrate(database: SupportSQLiteDatabase) {

        // Создаём failedHabit
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS failedHabit (
                id INTEGER NOT NULL PRIMARY KEY,
                taskId INTEGER NOT NULL,
                date TEXT NOT NULL,
                reasonSelected INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Уникальная пара taskId + date
        database.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS index_failedHabit_taskId_date
            ON failedHabit(taskId, date)
            """.trimIndent()
        )

        // Создаём statFailHabit
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS statFailHabit (
                id INTEGER NOT NULL,
                notPower INTEGER NOT NULL,
                notTime INTEGER NOT NULL,
                forgot INTEGER NOT NULL,
                other INTEGER NOT NULL,
                monthKey TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )

        // Начальная статистика
        database.execSQL(
            """
            INSERT OR IGNORE INTO statFailHabit (
                id,
                notPower,
                notTime,
                forgot,
                other,
                monthKey
            )
            VALUES (
                1,
                0,
                0,
                0,
                0,
                strftime('%Y-%m', 'now')
            )
            """.trimIndent()
        )
    }
}