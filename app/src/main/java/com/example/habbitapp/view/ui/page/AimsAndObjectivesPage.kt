package com.example.habbitapp.view.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.FilterChip
import com.example.habbitapp.R
import com.example.habbitapp.view.ui.card.AimCard
import com.example.habbitapp.view.ui.card.TaskCard
import com.example.habbitapp.view.ui.customElement.RowKalendarMy
import com.example.habbitapp.view.ui.theme.GrayLight
import com.example.habbitapp.view.ui.theme.GrayText
import com.example.habbitapp.view.ui.theme.GreenLight
import com.example.habbitapp.view.ui.theme.GreenPrimary
import com.example.habbitapp.view.ui.theme.White
import com.example.habbitapp.viewmodel.AimViewModel
import com.example.habbitapp.viewmodel.RowKalendarMyViewModel
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import io.github.chouaibmo.rowkalendar.RowKalendar
import io.github.chouaibmo.rowkalendar.components.DateCell
import io.github.chouaibmo.rowkalendar.components.DateCellDefaults
import io.github.chouaibmo.rowkalendar.extensions.now
import kotlinx.datetime.LocalDate

@Composable
fun AimsAndObjectibesPage(
    toMainPageClick: () -> Unit,
    toSettingsPageClick: () -> Unit,
    toProductivityPageClick: () -> Unit,
    toAddAimsPageClick: () -> Unit,
    onAimsClick: (Int) -> Unit,
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val viewModel: AimViewModel = viewModel()
    val tasks by viewModel.aim.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val viewModelUI: RowKalendarMyViewModel = viewModel { RowKalendarMyViewModel() }
    val uiState = viewModelUI.uiState.value
    val listState  = rememberLazyListState(
        initialFirstVisibleItemIndex = (uiState.dates.size / 2) - 1,
        initialFirstVisibleItemScrollOffset = -10
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(R.string.drawer_navigation), modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_habits)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.habit_ic),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = { toMainPageClick() }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_tasks)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.mission_ic),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_productivity)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.graphic_ic),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = { toProductivityPageClick() }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_settings)) },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = { toSettingsPageClick() }
                )

            }
        }
    ) {


        Column(modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                }) {
                    Icon(Icons.Filled.Menu, stringResource(R.string.cd_menu))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

//                    Text("Цели", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        stringResource(R.string.screen_tasks),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Green
                    )
                    Button(onClick = {
                        scope.launch {
                            val date = LocalDate.now()
                            val list = viewModelUI.uiState.value.dates
                            val index = binarySearchIndexDate(list,date)
                            listState.animateScrollToItem(index)
                        }
                    }, modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )) {
                        Text(
                            text = "Today",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            )
                    }
                }

            }

            RowKalendarMy(
                modifier = Modifier.fillMaxWidth(),
                scrollState = listState,
                content = { date, isSelected, onClick ->
                    DateCell(
                        date = date,
                        isSelected = isSelected,
                        shape = RoundedCornerShape(12.dp),
                        elevation = DateCellDefaults.DateCellElevation(
                            selectedElevation = 4.dp,
                            pastElevation = 2.dp,
                            futureElevation = 2.dp
                        ),
                        border = DateCellDefaults.border(
                            selectedBorderColor = Color.LightGray,
                            pastBorderColor = Color.LightGray,
                            futureBorderColor = Color.LightGray,
                            selectedBorderWidth = 1.dp,
                            pastBorderWidth = 1.dp,
                            futureBorderWidth = 1.dp
                        ),
                        colors = DateCellDefaults.colors(
                            selectedContainerColor = GreenPrimary,
                            selectedTextColor = White,

                            pastContainerColor = GrayLight,
                            pastTextColor = GrayText,

                            futureContainerColor = GreenLight,
                            futureTextColor = White
                        ),
                        modifier = Modifier,
                        onDateSelected = { clickedDate ->
                            onClick(clickedDate)
                            selectedDate = clickedDate
                        },
                    )
                }
            )




            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                val list = tasks.filter { it.date == selectedDate.toString() }

                items(list.filter { it.priority == 3 }) { task ->
                    AimCard(task, onUpdatePage = {
                        onAimsClick(task.id)
                    })
                }
                items(list.filter { it.priority == 2 }) { task ->
                    AimCard(task, onUpdatePage = {
                        onAimsClick(task.id)
                    })
                }
                items(list.filter { it.priority == 1 }) { task ->
                    AimCard(task, onUpdatePage = {
                        onAimsClick(task.id)
                    })
                }
                items(list.filter { it.priority == 0 }) { task ->
                    AimCard(task, onUpdatePage = {
                        onAimsClick(task.id)
                    })
                }
            }


        }
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            FloatingActionButton(
                onClick = toAddAimsPageClick,
                containerColor = GreenPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_add))
            }
        }
    }
}


@Preview
@Composable
fun AimsAndObjectibesPagePreview() {
    AimsAndObjectibesPage(

        toMainPageClick = {},
        toAddAimsPageClick = {},
        onAimsClick = {},
        toSettingsPageClick = {},
        toProductivityPageClick = {}
    )
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

