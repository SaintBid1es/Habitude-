package com.example.habbitapp.model.utils

import com.example.habbitapp.model.entity.Task
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class StreakManagerTest {

    private val fixedToday = LocalDate.of(2026, 4, 30)

    @Before
    fun setUp() {
        StreakManager.todayProvider = { fixedToday }
    }

    @After
    fun tearDown() {
        StreakManager.todayProvider = { LocalDate.now() }
    }

    @Test
    fun resetCheckExecForNewDay_whenTodayNotCompleted_resetsCheckExec() {
        val task = dailyTask(
            checkExec = true,
            completionDates = listOf("2026-04-29"),
        )

        val result = StreakManager.resetCheckExecForNewDay(task)

        assertFalse(result.checkExec)
    }

    @Test
    fun resetCheckExecForNewDay_whenTodayCompleted_keepsCheckExec() {
        val task = dailyTask(
            checkExec = true,
            completionDates = listOf("2026-04-30"),
        )

        val result = StreakManager.resetCheckExecForNewDay(task)

        assertTrue(result.checkExec)
    }

    @Test
    fun onTaskCompleted_daily_addsTodayAndSetsStreakToOne() {
        val task = dailyTask(streak = 0, completionDates = emptyList())

        val result = StreakManager.onTaskCompleted(task)

        assertTrue(result.checkExec)
        assertEquals(1, result.streak)
        assertTrue(result.completionDates.contains("2026-04-30"))
    }

    @Test
    fun onTaskCompleted_daily_whenAlreadyCompletedToday_doesNotDuplicateDate() {
        val task = dailyTask(
            streak = 1,
            completionDates = listOf("2026-04-30"),
            checkExec = false,
        )

        val result = StreakManager.onTaskCompleted(task)

        assertTrue(result.checkExec)
        assertEquals(1, result.completionDates.count { it == "2026-04-30" })
    }

    @Test
    fun onTaskUncompleted_daily_removesTodayAndResetsStreak() {
        val task = dailyTask(
            streak = 3,
            completionDates = listOf("2026-04-28", "2026-04-29", "2026-04-30"),
            checkExec = true,
        )

        val result = StreakManager.onTaskUncompleted(task)

        assertFalse(result.checkExec)
        assertFalse(result.completionDates.contains("2026-04-30"))
        assertEquals(0, result.streak)
    }

    @Test
    fun onTaskCompleted_weekly_onlyCountsOnScheduledDays() {
        // fixedToday = 2026-04-30 (Thursday, index 3)
        val task = Task(
            id = 1,
            name = "Weekly",
            streak = 0,
            icon = "📚",
            description = "",
            backgroundColor = 0xFFFFFFFF.toInt(),
            checkExec = false,
            repeat = 2,
            days = List(7) { index -> index == 3 },
            completionDates = emptyList(),
        )

        val result = StreakManager.onTaskCompleted(task)

        assertTrue(result.checkExec)
        assertEquals(1, result.streak)
        assertEquals(listOf("2026-04-30"), result.completionDates)
    }

    private fun dailyTask(
        streak: Int = 0,
        completionDates: List<String> = emptyList(),
        checkExec: Boolean = false,
    ) = Task(
        id = 1,
        name = "Read",
        streak = streak,
        icon = "📚",
        description = "",
        backgroundColor = 0xFFFFFFFF.toInt(),
        checkExec = checkExec,
        repeat = 1,
        days = List(7) { true },
        completionDates = completionDates,
    )
}
