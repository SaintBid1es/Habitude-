package com.example.habbitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbitapp.model.dao.FailedHabitDao
import com.example.habbitapp.model.dao.StatFailHabitDao
import com.example.habbitapp.model.entity.FailedHabit
import com.example.habbitapp.model.entity.FailedHabitWithTask
import com.example.habbitapp.model.entity.StatFailHabit
import com.example.habbitapp.view.ui.page.FailedHabitReason
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth

class FailedHabitViewModel(
    private val failedHabitDao: FailedHabitDao,
    private val statFailHabitDao: StatFailHabitDao
) : ViewModel() {

    val unansweredCount: StateFlow<Int> =
        failedHabitDao
            .observeUnansweredCount()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )


    val unanswered: StateFlow<List<FailedHabitWithTask>> =
        failedHabitDao
            .observeUnansweredWithTask()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    fun markReasonSelected(
        failedHabitId: Int,
        reason: FailedHabitReason
    ) {

        viewModelScope.launch {

            val currentMonth =
                YearMonth
                    .now()
                    .toString()

            val stat =
                statFailHabitDao
                    .getStatFailHabit()


            /*
             * Если статистики нет
             * или она относится к старому месяцу,
             * создаём новую статистику.
             */

            val updatedStat =

                if (
                    stat == null ||
                    stat.monthKey != currentMonth
                ) {

                    StatFailHabit(

                        id = 1,

                        notPower =
                            if (
                                reason ==
                                FailedHabitReason.NOT_POWER
                            ) 1 else 0,

                        notTime =
                            if (
                                reason ==
                                FailedHabitReason.NOT_TIME
                            ) 1 else 0,

                        forgot =
                            if (
                                reason ==
                                FailedHabitReason.FORGOT
                            ) 1 else 0,

                        other =
                            if (
                                reason ==
                                FailedHabitReason.OTHER
                            ) 1 else 0,

                        monthKey =
                            currentMonth
                    )

                } else {

                    when (reason) {

                        FailedHabitReason.NOT_POWER ->
                            stat.copy(
                                notPower =
                                    stat.notPower + 1
                            )

                        FailedHabitReason.NOT_TIME ->
                            stat.copy(
                                notTime =
                                    stat.notTime + 1
                            )

                        FailedHabitReason.FORGOT ->
                            stat.copy(
                                forgot =
                                    stat.forgot + 1
                            )

                        FailedHabitReason.OTHER ->
                            stat.copy(
                                other =
                                    stat.other + 1
                            )
                    }
                }


            if (
                stat == null ||
                stat.monthKey != currentMonth
            ) {

                statFailHabitDao.insert(
                    updatedStat
                )

            } else {

                statFailHabitDao.update(
                    updatedStat
                )
            }


            /*
             * Только после сохранения статистики
             * отмечаем пропуск обработанным.
             */

            failedHabitDao.markReasonSelected(
                failedHabitId
            )
        }
    }
}