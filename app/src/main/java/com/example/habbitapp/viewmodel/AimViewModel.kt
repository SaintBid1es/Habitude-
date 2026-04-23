package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.ItemDatabase
import com.example.habbitapp.MyApplication
import com.example.habbitapp.entity.Aims
import com.example.habbitapp.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AimViewModel : ViewModel() {
    private val dao by lazy {
        ItemDatabase.getInstance(MyApplication.appContext).aimsDao()
    }

    private val _aims = MutableStateFlow<List<Aims>>(emptyList())
    val aim: StateFlow<List<Aims>> = _aims

    init {

        viewModelScope.launch {
            dao.getAllAims().collectLatest { aimList ->
                _aims.value = aimList
            }
        }
    }

    fun insertAim(aim: Aims) = viewModelScope.launch {
        dao.insert(aim)
    }

    fun updateAim(aim: Aims) = viewModelScope.launch {
        dao.update(aim)
    }

    fun deleteAim(aim: Aims) = viewModelScope.launch {
        dao.delete(aim)
    }

    suspend fun findByIdAim(id: Int): Aims? {
        return dao.getAimById(id)
    }
    suspend fun deleteByIdAims(id: Int) {
        return dao.deleteAimsById(id)
    }

}