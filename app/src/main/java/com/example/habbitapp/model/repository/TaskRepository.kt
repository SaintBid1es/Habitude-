package com.example.habbitapp.model.repository

import com.example.habbitapp.model.entity.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAllTasks(): Flow<List<Task>>
    suspend fun insert(task: Task)
    suspend fun insertAndGetId(task: Task): Long
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun getCount(): Int
    suspend fun getIndicator(date: String, totalTasks: Int): Int
    suspend fun getById(id: Int): Task?
    suspend fun deleteById(id: Int)
}
