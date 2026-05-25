package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.MyApplication
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.model.entity.Task
import com.example.habbitapp.model.repository.TaskRepository
import com.example.habbitapp.model.repository.TaskRepositoryImpl
import kotlinx.coroutines.Dispatchers
import com.example.habbitapp.view.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(
    private val repository: TaskRepository = defaultRepository(),
) : ViewModel() {

    private val _tasksState = MutableStateFlow<UiState<List<Task>>>(UiState.Loading)
    val tasksState: StateFlow<UiState<List<Task>>> = _tasksState

    init {
        observeTasks()
    }

    fun reloadTasks() {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            repository.observeAllTasks()
                .catch { e ->
                    _tasksState.value = UiState.Error(
                        message = e.message ?: "Failed to load habits",
                    )
                }
                .collectLatest { taskList ->
                    _tasksState.value = UiState.Success(taskList)
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