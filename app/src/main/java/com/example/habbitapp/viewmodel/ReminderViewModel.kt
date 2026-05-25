package com.example.habbitapp.viewmodel

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.MyApplication
import com.example.habbitapp.model.entity.Reminder
import com.example.habbitapp.model.repository.AimRepository
import com.example.habbitapp.model.repository.AimRepositoryImpl
import com.example.habbitapp.model.repository.ReminderImpl
import com.example.habbitapp.model.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class ReminderViewModel(
    private val repository: ReminderRepository = reminderRepository()
) : ViewModel() {
    private val dao by lazy {
        ItemDatabase.getInstance(MyApplication.appContext).reminderDao()
    }
    private val _reminder = MutableStateFlow<List<Reminder>>(emptyList())
    val reminder: StateFlow<List<Reminder>> = _reminder

    init {

        viewModelScope.launch {
            repository.observeAllReminder().collectLatest { reminderList ->
                _reminder.value = reminderList
            }
        }
    }

    fun insertReminder(reminder: Reminder) = viewModelScope.launch {
        repository.insertReminder(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        repository.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        dao.delete(reminder)
    }

    suspend fun findByIdReminderTask(id: Int): Reminder? {
        return repository.findByIdReminderTask(id)
    }
    suspend fun deleteByIdReminder(id: Int) {
        return dao.deleteReminderById(id)
    }

    companion object {
        private fun reminderRepository(): ReminderRepository {
            val dao = ItemDatabase.getInstance(MyApplication.appContext).reminderDao()
            return ReminderImpl(dao)
        }
    }

}