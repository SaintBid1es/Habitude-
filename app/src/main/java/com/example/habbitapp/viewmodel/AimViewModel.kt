package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.MyApplication
import com.example.habbitapp.model.entity.Aims
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

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
    fun binarySearchIndexDate(list: List<LocalDate>,date:LocalDate): Int {
        var low = 0
        var high  = list.size - 1
        var mid:Int = 0
        while (low<=high){
            mid = (low+high) / 2
            var guess = list[mid]
            if (guess == date) return mid
            else if (guess>date){
                high = mid-1
            }
            else {
                low = mid+1
            }
        }

        return mid
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

    suspend fun findByIdAim(id: Int): Aims {
        return dao.getAimById(id)
    }
    suspend fun deleteByIdAims(id: Int) {
        return dao.deleteAimsById(id)
    }
    suspend fun migrateUnfinishedTasks(today:String){
        viewModelScope.launch {
            dao.migrateOldTasks(today)
        }
    }

}