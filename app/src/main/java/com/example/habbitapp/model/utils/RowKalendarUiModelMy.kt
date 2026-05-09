package com.example.habbitapp.model.utils

import io.github.chouaibmo.rowkalendar.extensions.now
import kotlinx.datetime.LocalDate

internal data class RowKalendarUiMyModel(
    val isLoading: Boolean = false,
    val dates: List<LocalDate> = listOf(),
    val selectedDate: LocalDate = LocalDate.now(),
)