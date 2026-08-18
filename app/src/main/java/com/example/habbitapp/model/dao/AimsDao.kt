package com.example.habbitapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habbitapp.model.entity.Aims
import kotlinx.coroutines.flow.Flow


@Dao
interface AimsDao {
    @Insert
    suspend fun insert(aim: Aims)

    @Update
    suspend fun update(aim: Aims)

    @Delete
    suspend fun delete(aim: Aims)

    @Query("SELECT * FROM aims ORDER BY id DESC")
    fun getAllAims(): Flow<List<Aims>>

    @Query("SELECT COUNT(*) FROM aims")
    fun getCountBlocking(): Int

    @Query("SELECT * FROM aims WHERE id = :id")
    suspend fun getAimById(id: Int): Aims

    @Query("Delete from aims WHERE id = :id")
    suspend fun deleteAimsById(id: Int)


    @Query("UPDATE aims SET date = :newDate WHERE checkExec = 0 AND autotransfer = 1 AND date < :newDate")
    suspend fun migrateOldTasks(newDate: String)
    // Блокирующий метод для получения количества целей на сегодня
    @Query("SELECT COUNT(*) FROM aims WHERE date = :todayDate")
    fun getCountForDateBlocking(todayDate: String): Int

    // Suspend метод для получения количества целей на сегодня
    @Query("SELECT COUNT(*) FROM aims WHERE date = :todayDate")
    suspend fun getCountForDate(todayDate: String): Int

    // Блокирующий метод для получения активных целей на сегодня
    @Query("SELECT COUNT(*) FROM aims WHERE date = :todayDate AND checkExec = 0")
    fun getActiveCountForDateBlocking(todayDate: String): Int

    // Suspend метод для получения активных целей на сегодня


    // Получить все цели на сегодня
    @Query("SELECT * FROM aims WHERE date = :todayDate")
    suspend fun getAimsForDate(todayDate: String): List<Aims>

    // Получить активные цели на сегодня
    @Query("SELECT * FROM aims WHERE date = :todayDate AND checkExec = 0")
    suspend fun getActiveAimsForDate(todayDate: String): List<Aims>

    @Query("SELECT COUNT(*) FROM aims WHERE date = :date AND checkExec = 0")
    suspend fun getActiveCountForDate(date: String): Int


}