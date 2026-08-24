package com.example.habbitapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habbitapp.model.entity.StatFailHabit
import com.example.habbitapp.model.entity.Task

@Dao
interface StatFailHabitDao {


    @Insert
    suspend fun insertTaskAndGetId(stat: StatFailHabit): Long

    @Update
    suspend fun update(stat: StatFailHabit)

    @Delete
    suspend fun delete(stat: StatFailHabit)

    @Query("SELECT * FROM statFailHabit WHERE id = :id")
    suspend fun getStatFailHabitById(id: Int): StatFailHabit?

    @Query("SELECT * FROM statFailHabit WHERE id = 1 LIMIT 1")
    suspend fun getStatFailHabit(): StatFailHabit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: StatFailHabit)


    @Query("""
        UPDATE statFailHabit
        SET notPower = 0,
            notTime = 0,
            forgot = 0,
            other = 0,
            monthKey = :monthKey
        WHERE id = 1
    """)
    suspend fun resetStats(monthKey: String)


}