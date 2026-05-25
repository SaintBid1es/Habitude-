package com.example.habbitapp.model.repository

import com.example.habbitapp.model.entity.Aims
import kotlinx.datetime.LocalDate

interface AimRepository {
   suspend fun deleteByIdAims(id: Int)
    suspend fun findByIdAim(id: Int): Aims
    suspend fun migrateUnfinishedTasks(today:String)
    suspend  fun updateAim(aim: Aims)
    suspend fun insertAim(aim: Aims)
    fun binarySearchIndexDate(list: List<LocalDate>,date:LocalDate): Int
}

