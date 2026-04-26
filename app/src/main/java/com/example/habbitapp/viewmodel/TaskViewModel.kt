package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.MyApplication
import com.example.habbitapp.model.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class TaskViewModel() : ViewModel() {
    private val dao by lazy {
        ItemDatabase.getInstance(MyApplication.appContext).taskDao()
    }

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val task: StateFlow<List<Task>> = _tasks

    init {

        viewModelScope.launch {
            dao.getAllTask().collectLatest { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        dao.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        dao.update(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        dao.delete(task)
    }

    suspend fun findByIdTask(id: Int): Task? {
        return dao.getTaskById(id)
    }
    suspend fun deleteByIdTask(id: Int) {
        return dao.deleteTaskById(id)
    }
    suspend fun insertTaskAndGetId(task: Task): Long {
        return dao.insertTaskAndGetId(task)
    }


}