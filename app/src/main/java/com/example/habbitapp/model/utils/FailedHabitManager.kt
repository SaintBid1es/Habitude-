package com.example.habbitapp.model.utils

import android.content.Context
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.model.entity.FailedHabit
import com.example.habbitapp.model.entity.Task
import java.time.LocalDate


object FailedHabitManager {

    suspend fun checkMissedHabits(
        context: Context,
        tasks: List<Task>
    ) {

        val dao = ItemDatabase
            .getInstance(context)
            .failedHabitDao()

        val yesterday = LocalDate.now().minusDays(1)

        val yesterdayIndex =
            yesterday.dayOfWeek.value - 1

        tasks
            .filter { task ->

                task.days.getOrNull(yesterdayIndex) == true
            }
            .filter { task ->

                !task.completionDates.contains(
                    yesterday.toString()
                )
            }
            .forEach { task ->

                if (!dao.exists(
                        taskId = task.id,
                        date = yesterday.toString()
                    )
                ) {

                    dao.insert(
                        FailedHabit(
                            taskId = task.id,
                            date = yesterday.toString(),
                            reasonSelected = false
                        )
                    )
                }
            }
    }
}