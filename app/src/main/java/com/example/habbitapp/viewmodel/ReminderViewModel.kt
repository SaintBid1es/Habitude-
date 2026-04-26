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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class ReminderViewModel() : ViewModel() {
    private val dao by lazy {
        ItemDatabase.getInstance(MyApplication.appContext).reminderDao()
    }
    companion object{
         const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
         const val NOTIFICATION_ID = 1
    }

    private val _reminder = MutableStateFlow<List<Reminder>>(emptyList())
    val reminder: StateFlow<List<Reminder>> = _reminder

    init {

        viewModelScope.launch {
            dao.getAllReminder().collectLatest { reminderList ->
                _reminder.value = reminderList
            }
        }
    }

    fun insertReminder(reminder: Reminder) = viewModelScope.launch {
        dao.insert(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        dao.update(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        dao.delete(reminder)
    }

    suspend fun findByIdReminderTask(id: Int): Reminder? {
        return dao.getReminderByIdTask(id)
    }
    suspend fun deleteByIdReminder(id: Int) {
        return dao.deleteReminderById(id)
    }

     fun requestNotificationPermission(activity: Activity) {
        // Для Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

}