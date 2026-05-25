package com.example.habbitapp.model.repository

import com.example.habbitapp.model.dao.TaskDao
import com.example.habbitapp.model.entity.Task
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
) : TaskRepository {

    override fun observeAllTasks(): Flow<List<Task>> = taskDao.getAllTask()

    override suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    override suspend fun insertAndGetId(task: Task): Long = taskDao.insertTaskAndGetId(task)

    override suspend fun update(task: Task) {
        taskDao.update(task)
    }

    override suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    override suspend fun getCount(): Int = taskDao.getAllTaskCount()

    override suspend fun getIndicator(date: String, totalTasks: Int): Int =
        taskDao.getIndicator(date, totalTasks)

    override suspend fun getById(id: Int): Task? = taskDao.getTaskById(id)

    override suspend fun deleteById(id: Int) {
        taskDao.deleteTaskById(id)
    }
}
