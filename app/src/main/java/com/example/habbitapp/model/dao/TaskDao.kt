package com.example.habbitapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habbitapp.model.entity.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Insert
    suspend fun insertTaskAndGetId(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task ORDER BY id DESC")
    fun getAllTask(): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task ")
   suspend fun getAllTaskCount(): Int

    @Query("SELECT (COUNT(CASE WHEN completionDates = :date THEN 1 END) * 100 / :count) FROM task")
    fun getIndicator(date: String,count:Int): Int
    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<Task>


    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Query("Delete from task WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Query("SELECT COUNT(*) FROM task")
    fun getCountBlocking(): Int


    @Query("SELECT COUNT(*) FROM task")
    suspend fun getCountSuspend(): Int

    @Query("SELECT * FROM task")
    fun getAllTasksBlocking(): List<Task>

    @Query("SELECT * FROM task WHERE checkExec = false")
    fun getAllTasksBlockingForNotification(): List<Task>
    suspend fun getTasksForToday(): List<Task> {
        val allTasks = getAllTasksBlocking()
        val today = java.time.LocalDate.now().dayOfWeek.value - 1
        return allTasks.filter { it.days.getOrNull(today) == true }
    }
//    @Query("SELECT * FROM task WHERE completionDates= :date AND checkExec=false ")
//    fun getAllHabitFailToday(date:String) : List<Int>

    suspend fun getCountForToday(): Int {
        return getTasksForToday().size
    }

}