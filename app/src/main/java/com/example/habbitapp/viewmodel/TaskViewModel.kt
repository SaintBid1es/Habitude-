package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.MyApplication
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.model.entity.Task
import com.example.habbitapp.model.repository.TaskRepository
import com.example.habbitapp.model.repository.TaskRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

class TaskViewModel(
    private val repository: TaskRepository = defaultRepository(),
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val task: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            repository.observeAllTasks().collectLatest { taskList ->
                _tasks.value = taskList
            }
        }
    }

    suspend fun getCountTask(): Int = withContext(Dispatchers.IO) {
        repository.getCount()
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    suspend fun getIndicator(date: String, allTasks: Int): Int = withContext(Dispatchers.IO) {
        repository.getIndicator(date, allTasks)
    }

    suspend fun findByIdTask(id: Int): Task? = repository.getById(id)

    suspend fun deleteByIdTask(id: Int) {
        repository.deleteById(id)
    }

    suspend fun insertTaskAndGetId(task: Task): Long = repository.insertAndGetId(task)

    companion object {
        private fun defaultRepository(): TaskRepository {
            val dao = ItemDatabase.getInstance(MyApplication.appContext).taskDao()
            return TaskRepositoryImpl(dao)
        }
    }
}