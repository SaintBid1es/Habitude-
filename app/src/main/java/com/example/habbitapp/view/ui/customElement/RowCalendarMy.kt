package com.example.habbitapp.view.ui.customElement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.LocalDate
@Composable
fun RowKalendarMy(
    modifier: Modifier = Modifier,
    isBounded: Boolean = true,
    maxDays: Int = 365,
    scrollState: LazyListState,
    content: @Composable (date: LocalDate, isSelected: Boolean, onClick: (LocalDate) -> Unit) -> Unit
) {
    val viewModel: RowKalendarMyViewModel = viewModel { RowKalendarMyViewModel() }
    val uiState = viewModel.uiState.value
//    val scrollState = rememberLazyListState(
//        initialFirstVisibleItemIndex = (uiState.dates.size / 2) - 1,
//        initialFirstVisibleItemScrollOffset = -10
//    )

    viewModel.setMaxDaysToLoad(maxDays)
    viewModel.setIsBounded(isBounded)

    LazyRow(
        state = scrollState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        itemsIndexed(uiState.dates) { index, date ->

            if (index == 0 && !uiState.isLoading){
                viewModel.loadPreviousDates()
            }
            else if (index == uiState.dates.size - 1 && !uiState.isLoading) {
                viewModel.loadUpcomingDates()
            }

            val isSelected = date == uiState.selectedDate
            content(date, isSelected) {
                viewModel.selectDate(date)
            }

        }
    }
}