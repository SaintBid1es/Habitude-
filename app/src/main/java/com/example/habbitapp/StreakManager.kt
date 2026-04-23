package com.example.habbitapp.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habbitapp.entity.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object StreakManager {

    @SuppressLint("NewApi")
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Проверяет, должна ли привычка выполняться в указанную дату
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun shouldCompleteOnDate(task: Task, date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek.value - 1

        return when (task.repeat) {
            1 -> true
            2 -> task.days.getOrElse(dayOfWeek) { false }
            3 -> date.dayOfMonth == 1
            else -> false
        }
    }

    /**
     * Находит последнюю дату, когда привычка была выполнена в нужный день
     */
    @SuppressLint("NewApi")
    private fun getLastValidCompletionDate(task: Task): LocalDate? {
        val sortedDates = task.completionDates
            .map { LocalDate.parse(it, dateFormatter) }
            .sortedDescending()

        for (date in sortedDates) {
            if (shouldCompleteOnDate(task, date)) {
                return date
            }
        }
        return null
    }

    /**
     * Считает, сколько РАБОЧИХ дней было между двумя датами (включая конечную)
     */
    @SuppressLint("NewApi")
    private fun countRequiredDaysBetween(task: Task, startDate: LocalDate, endDate: LocalDate): Int {
        var count = 0
        var currentDate = startDate.plusDays(1)

        while (!currentDate.isAfter(endDate)) {
            if (shouldCompleteOnDate(task, currentDate)) {
                count++
            }
            currentDate = currentDate.plusDays(1)
        }

        return count
    }

    /**
     * Когда пользователь ВЫПОЛНИЛ привычку (поставил галочку)
     */
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

        val lastValidDate = getLastValidCompletionDate(
            task.copy(completionDates = newCompletionDates)
        )

        val newStreak = if (lastValidDate == null) {
            1
        } else {
            val requiredDaysBetween = countRequiredDaysBetween(task, lastValidDate, today)

            if (requiredDaysBetween == 1) {

                task.streak + 1
            } else {

                1
            }
        }

        return task.copy(
            streak = newStreak,
            completionDates = newCompletionDates,
            checkExec = true
        )
    }

    /**
     * Когда пользователь ОТМЕНИЛ выполнение (снял галочку)
     */
    @SuppressLint("NewApi")
    fun onTaskUncompleted(task: Task): Task {
        val today = LocalDate.now()
        val todayString = today.format(dateFormatter)

        if (!task.completionDates.contains(todayString)) {
            return task.copy(checkExec = false)
        }

        val newCompletionDates = task.completionDates.filter { it != todayString }

        val lastValidDate = getLastValidCompletionDate(
            task.copy(completionDates = newCompletionDates)
        )

        val newStreak = if (lastValidDate == null) {
            0
        } else {
            val todayDate = LocalDate.now()
            val daysBetween = ChronoUnit.DAYS.between(lastValidDate, todayDate)

            if (daysBetween <= 1) {
                calculateStreakFromDates(newCompletionDates)
            } else {

                0
            }
        }

        return task.copy(
            streak = newStreak,
            completionDates = newCompletionDates,
            checkExec = false
        )
    }

    /**
     * Рассчитываем streak на основе списка дат выполнений (только для ежедневных привычек)
     * Для НЕежедневных используем другой расчёт
     */
    @SuppressLint("NewApi")
    private fun calculateStreakFromDates(dates: List<String>): Int {
        if (dates.isEmpty()) return 0

        val sortedDates = dates
            .map { LocalDate.parse(it, dateFormatter) }
            .sorted()

        var currentStreak = 1
        var maxStreak = 1

        for (i in 1 until sortedDates.size) {
            val prevDate = sortedDates[i - 1]
            val currDate = sortedDates[i]

            if (ChronoUnit.DAYS.between(prevDate, currDate) == 1L) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        val lastDate = sortedDates.last()
        val today = LocalDate.now()

        return if (ChronoUnit.DAYS.between(lastDate, today) <= 1) {
            currentStreak
        } else {
            0
        }
    }

    /**
     * Сброс галочки в начале нового дня
     */
    @SuppressLint("NewApi")
    fun resetCheckExecForNewDay(task: Task): Task {
        val today = LocalDate.now().format(dateFormatter)

        return if (!task.completionDates.contains(today) && task.checkExec) {
            task.copy(checkExec = false)
        } else {
            task
        }
    }

    /**
     * Получить текущий streak (для отображения)
     */
    @SuppressLint("NewApi")
    fun getCurrentStreak(task: Task): Int {
        val today = LocalDate.now()
        val lastValidDate = getLastValidCompletionDate(task)

        if (lastValidDate == null) return 0

        val daysBetween = ChronoUnit.DAYS.between(lastValidDate, today)

        return if (daysBetween <= 1) {
            task.streak
        } else {
            0
        }
    }
}