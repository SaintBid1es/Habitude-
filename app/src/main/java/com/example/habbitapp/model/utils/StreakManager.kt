package com.example.habbitapp.model.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habbitapp.model.entity.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object StreakManager {

    @SuppressLint("NewApi")
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shouldCompleteOnDate(task: Task, date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek.value - 1

        return when (task.repeat) {
            1 -> true // Ежедневно
            2 -> task.days.getOrElse(dayOfWeek) { false }
            3 -> date.dayOfMonth == 1
            else -> false
        }
    }

    @SuppressLint("NewApi")
    fun onTaskCompleted(task: Task): Task {
        val today = LocalDate.now()
        val todayString = today.format(dateFormatter)

        if (!shouldCompleteOnDate(task, today)) {
            return task.copy(checkExec = true)
        }

        if (task.completionDates.contains(todayString)) {
            return task.copy(checkExec = true)
        }

        val newCompletionDates = (task.completionDates + todayString).sorted()
        val newStreak = calculateCurrentStreak(task, newCompletionDates)

        return task.copy(
            streak = newStreak,
            completionDates = newCompletionDates,
            checkExec = true
        )
    }

    @SuppressLint("NewApi")
    fun onTaskUncompleted(task: Task): Task {
        val today = LocalDate.now()
        val todayString = today.format(dateFormatter)

        if (!task.completionDates.contains(todayString)) {
            return task.copy(checkExec = false)
        }

        val newCompletionDates = task.completionDates.filter { it != todayString }
        val newStreak = calculateCurrentStreak(task, newCompletionDates)

        return task.copy(
            streak = newStreak,
            completionDates = newCompletionDates,
            checkExec = false
        )
    }

    @SuppressLint("NewApi")
    private fun calculateCurrentStreak(task: Task, completionDates: List<String>): Int {
        if (completionDates.isEmpty()) return 0

        val sortedDates = completionDates
            .map { LocalDate.parse(it, dateFormatter) }
            .sortedDescending()

        return when (task.repeat) {
            1 -> calculateDailyStreak(sortedDates)
            2 -> calculateWeeklyStreak(task, sortedDates)
            3 -> calculateMonthlyStreak(sortedDates)
            else -> 0
        }
    }

    @SuppressLint("NewApi")
    private fun calculateDailyStreak(sortedDates: List<LocalDate>): Int {
        if (sortedDates.isEmpty()) return 0

        val today = LocalDate.now()
        var streak = 0
        var expectedDate = today

        for (date in sortedDates) {
            when {
                date == expectedDate -> {
                    streak++
                    expectedDate = expectedDate.minusDays(1)
                }
                date.isBefore(expectedDate) -> {

                    break
                }
                else -> {

                    break
                }
            }
        }


        val lastCompletionDate = sortedDates.first()
        val daysSinceLastCompletion = ChronoUnit.DAYS.between(lastCompletionDate, today)

        return if (daysSinceLastCompletion <= 1) {
            streak
        } else {
            0
        }
    }

    @SuppressLint("NewApi")
    private fun calculateWeeklyStreak(task: Task, sortedDates: List<LocalDate>): Int {
        if (sortedDates.isEmpty()) return 0

        val today = LocalDate.now()
        val completedDatesSet = sortedDates.toSet()

        var currentWorkingDay = findLastWorkingDay(task, today)

        if (currentWorkingDay == null) return 0


        if (!completedDatesSet.contains(currentWorkingDay) && currentWorkingDay != today) {
            return 0
        }

        if (currentWorkingDay == today && !completedDatesSet.contains(today)) {
            val previousDay = findPreviousWorkingDay(task, today)
            if (previousDay != null && completedDatesSet.contains(previousDay)) {
                currentWorkingDay = previousDay
            } else {
                return 0
            }
        }

        var streak = 0

        while (currentWorkingDay != null) {
            if (completedDatesSet.contains(currentWorkingDay)) {
                streak++
                currentWorkingDay = findPreviousWorkingDay(task, currentWorkingDay)
            } else {
                break
            }
        }

        return streak
    }

    @SuppressLint("NewApi")
    private fun calculateMonthlyStreak(sortedDates: List<LocalDate>): Int {
        if (sortedDates.isEmpty()) return 0

        val today = LocalDate.now()
        var streak = 0
        var expectedYearMonth = today.withDayOfMonth(1)

        for (date in sortedDates) {
            val dateYearMonth = date.withDayOfMonth(1)
            when {
                dateYearMonth == expectedYearMonth -> {
                    streak++
                    expectedYearMonth = expectedYearMonth.minusMonths(1)
                }
                dateYearMonth.isBefore(expectedYearMonth) -> {
                    break
                }
                else -> {
                    break
                }
            }
        }

        val lastCompletionDate = sortedDates.first()
        val monthsSinceLastCompletion = ChronoUnit.MONTHS.between(
            lastCompletionDate.withDayOfMonth(1),
            today.withDayOfMonth(1)
        )

        return if (monthsSinceLastCompletion <= 1) {
            streak
        } else {
            0
        }
    }

    @SuppressLint("NewApi")
    private fun findLastWorkingDay(task: Task, fromDate: LocalDate): LocalDate? {
        var currentDate = fromDate

        val limitDate = fromDate.minusMonths(6)

        while (!currentDate.isBefore(limitDate)) {
            if (shouldCompleteOnDate(task, currentDate)) {
                return currentDate
            }
            currentDate = currentDate.minusDays(1)
        }

        return null
    }

    @SuppressLint("NewApi")
    private fun findPreviousWorkingDay(task: Task, fromDate: LocalDate): LocalDate? {
        var currentDate = fromDate.minusDays(1)

        val limitDate = fromDate.minusMonths(6)

        while (!currentDate.isBefore(limitDate)) {
            if (shouldCompleteOnDate(task, currentDate)) {
                return currentDate
            }
            currentDate = currentDate.minusDays(1)
        }

        return null
    }

    @SuppressLint("NewApi")
    fun resetCheckExecForNewDay(task: Task): Task {
        val today = LocalDate.now().format(dateFormatter)
        return if (!task.completionDates.contains(today) && task.checkExec) {
            task.copy(checkExec = false)
        } else {
            task
        }
    }

    @SuppressLint("NewApi")
    fun getCurrentStreak(task: Task): Int {
        return calculateCurrentStreak(task, task.completionDates)
    }
}