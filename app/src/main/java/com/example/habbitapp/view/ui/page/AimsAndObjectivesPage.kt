package com.example.habbitapp.view.ui.page

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.habbitapp.view.ui.theme.GrayLight
import com.example.habbitapp.view.ui.theme.GrayText
import com.example.habbitapp.view.ui.theme.GreenLight
import com.example.habbitapp.view.ui.theme.GreenPrimary
import com.example.habbitapp.view.ui.theme.White
import com.example.habbitapp.viewmodel.AimViewModel
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import io.github.chouaibmo.rowkalendar.RowKalendar
import io.github.chouaibmo.rowkalendar.components.DateCell
import io.github.chouaibmo.rowkalendar.components.DateCellDefaults
import kotlinx.datetime.LocalDate

@Composable
fun AimsAndObjectibesPage( modifier: Modifier = Modifier,toMainPageClick: ()-> Unit,toAddAimsPageClick: ()-> Unit,onAimsClick: (Int) -> Unit,
                           ) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val viewModel: AimViewModel = viewModel()
    val tasks by viewModel.aim.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Navigation", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Управление привычками") },
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
                    label = { Text("Управление целями и задачами") },
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

            }
        }
    ) {


        Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                }) {
                    Icon(Icons.Filled.Menu, "Меню")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

//                    Text("Цели", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        "Задачи",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Green
                    )
                }
            }

            RowKalendar(
                modifier = Modifier.fillMaxWidth(),
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
                        onDateSelected = { clickedDate->
                            onClick(clickedDate)
                            selectedDate = clickedDate
                        },
                    )
                }
            )




            LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {

                        items(tasks.filter { it.date == selectedDate.toString() }) { task ->
                            AimCard (task, onUpdatePage = {
                                onAimsClick(task.id)
                            })

                        }
                    }




        }
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize().padding(30.dp)
        ) {
            FloatingActionButton(
                onClick = toAddAimsPageClick,
                containerColor = GreenPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить")
            }
        }
    }
}


@Preview
@Composable
fun AimsAndObjectibesPagePreview(){
AimsAndObjectibesPage(

    toMainPageClick = {},
    toAddAimsPageClick = {},
    onAimsClick = {}
)
}




