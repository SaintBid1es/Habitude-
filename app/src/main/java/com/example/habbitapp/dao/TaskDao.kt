package com.example.habbitapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habbitapp.entity.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task )

    @Insert
    suspend fun insertTaskAndGetId(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task ORDER BY id DESC")
    fun getAllTask(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Query("Delete from task WHERE id = :id")
    suspend fun deleteTaskById(id: Int)


}