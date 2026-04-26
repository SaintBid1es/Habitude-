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

    @Query("SELECT * FROM aims WHERE id = :id")
    suspend fun getAimById(id: Int): Aims?

    @Query("Delete from aims WHERE id = :id")
    suspend fun deleteAimsById(id: Int)
}