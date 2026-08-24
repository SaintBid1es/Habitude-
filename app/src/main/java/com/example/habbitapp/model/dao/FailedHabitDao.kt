package com.example.habbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habbitapp.model.entity.FailedHabit
import com.example.habbitapp.model.entity.FailedHabitWithTask
import kotlinx.coroutines.flow.Flow

@Dao
interface FailedHabitDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(
        failedHabit: FailedHabit
    )

    @Query("""
        SELECT * 
        FROM failedHabit
        WHERE reasonSelected = 0
        ORDER BY date ASC
    """)
    fun observeUnanswered(): Flow<List<FailedHabit>>

    @Query("""
        SELECT COUNT(*)
        FROM failedHabit
        WHERE reasonSelected = 0
    """)
    fun observeUnansweredCount(): Flow<Int>

    @Query("""
        SELECT *
        FROM failedHabit
        WHERE reasonSelected = 0
        ORDER BY date ASC
    """)
    suspend fun getUnanswered(): List<FailedHabit>

    @Query("""
        SELECT *
        FROM failedHabit
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Int): FailedHabit?

    @Query("""
        SELECT 
            failedHabit.id AS failedHabitId,
            failedHabit.taskId AS taskId,
            failedHabit.date AS date,
            failedHabit.reasonSelected AS reasonSelected,
            Task.name AS taskName,
            Task.icon AS taskIcon
        FROM failedHabit
        INNER JOIN task
            ON failedHabit.taskId = Task.id
        WHERE failedHabit.reasonSelected = 0
        ORDER BY failedHabit.date ASC
    """)
    fun observeUnansweredWithTask(): Flow<List<FailedHabitWithTask>>





    @Query("""
        UPDATE failedHabit
        SET reasonSelected = 1
        WHERE id = :id
    """)
    suspend fun markReasonSelected(id: Int)

    @Query("""
        SELECT EXISTS(
            SELECT 1
            FROM failedHabit
            WHERE taskId = :taskId
            AND date = :date
        )
    """)
    suspend fun exists(
        taskId: Int,
        date: String
    ): Boolean
}