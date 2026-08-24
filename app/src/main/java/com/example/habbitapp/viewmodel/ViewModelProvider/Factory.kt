package com.example.habbitapp.viewmodel.ViewModelProvider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habbitapp.model.dao.FailedHabitDao
import com.example.habbitapp.model.dao.StatFailHabitDao
import com.example.habbitapp.viewmodel.FailedHabitViewModel

class FailedHabitViewModelFactory(
    private val failedHabitDao: FailedHabitDao,
    private val statFailHabitDao: StatFailHabitDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FailedHabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FailedHabitViewModel(
                failedHabitDao = failedHabitDao,
                statFailHabitDao = statFailHabitDao
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}