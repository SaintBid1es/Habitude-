package com.example.habbitapp.model.repository

import com.example.habbitapp.model.dao.AimsDao
import com.example.habbitapp.model.dao.TaskDao
import com.example.habbitapp.model.entity.Aims
import kotlinx.datetime.LocalDate

class AimRepositoryImpl(
    private val taskDao: AimsDao,
) : AimRepository {
    override suspend fun deleteByIdAims(id: Int) {
       return taskDao.deleteAimsById(id)
    }

    override suspend fun findByIdAim(id: Int): Aims {
      return  taskDao.getAimById(id)
    }

    override suspend fun migrateUnfinishedTasks(today: String) {
        taskDao.migrateOldTasks(today)
    }

    override suspend fun updateAim(aim: Aims) {
        taskDao.update(aim)
    }

    override suspend fun insertAim(aim: Aims) {
        taskDao.insert(aim)
    }

    override fun binarySearchIndexDate(
        list: List<LocalDate>,
        date: LocalDate
    ): Int {
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
}