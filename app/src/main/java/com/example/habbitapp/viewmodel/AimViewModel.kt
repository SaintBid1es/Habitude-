package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.MyApplication
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.model.repository.AimRepository
import com.example.habbitapp.model.repository.AimRepositoryImpl
import com.example.habbitapp.model.repository.TaskRepository
import com.example.habbitapp.model.repository.TaskRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AimViewModel(
    private val repository: AimRepository = aimRepository()
) : ViewModel() {
    private val _aims = MutableStateFlow<List<Aims>>(emptyList())
    val aim: StateFlow<List<Aims>> = _aims

    init {

        viewModelScope.launch {
            repository.observeAllAims().collectLatest { aimList ->
                _aims.value = aimList
            }
        }
    }
    fun binarySearchIndexDate(list: List<LocalDate>,date:LocalDate): Int {
      return  repository.binarySearchIndexDate(list,date)
    }
    fun insertAim(aim: Aims) = viewModelScope.launch {
       repository.insertAim(aim)
    }

    fun updateAim(aim: Aims) = viewModelScope.launch {
       repository.updateAim(aim)
    }

    suspend fun findByIdAim(id: Int): Aims {
        return repository.findByIdAim(id)
    }
    suspend fun deleteByIdAims(id: Int) {
        return repository.deleteByIdAims(id)
    }
     fun migrateUnfinishedTasks(today:String){
        viewModelScope.launch {
            repository.migrateUnfinishedTasks(today)
        }
    }
    companion object {
        internal fun aimRepository(): AimRepository {
            val dao = ItemDatabase.getInstance(MyApplication.appContext).aimsDao()
            return AimRepositoryImpl(dao)
        }
    }
}