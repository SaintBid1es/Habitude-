package com.example.habbitapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.habbitapp.ui.card.TaskCard
import com.example.habbitapp.ui.page.AimsAndObjectibesPage
import com.example.habbitapp.ui.theme.HabbitAppTheme
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabbitAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        val navController = rememberNavController()
                        AppNavHost(navController)
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun MainPage( modifier: Modifier = Modifier,toAddTaskPageClick: ()-> Unit,onTaskClick: (Int) -> Unit,
              toAimsAndObjectivesPageClick: ()-> Unit) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val viewModel: TaskViewModel = viewModel()
    val tasks  by viewModel.task.collectAsStateWithLifecycle()
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
                    icon = { Icon(painter = painterResource(R.drawable.habit_ic), contentDescription = null,modifier= Modifier.size(25.dp)) },
                    selected = false,
                    onClick = { scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Управление задачами") },
                    icon = { Icon(painter = painterResource(R.drawable.mission_ic), contentDescription = null,modifier= Modifier.size(25.dp)) },
                    selected = false,
                    onClick = { toAimsAndObjectivesPageClick() }
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
                    scope.launch {drawerState.open()} }) {
                    Icon(Icons.Filled.Menu, "Меню")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text("Habit", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        "App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Green
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FilterChip(
                    text = "Today",
                    isSelected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 }
                )
                FilterChip(
                    text = "Weekly",
                    isSelected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 }
                )
                FilterChip(
                    text = "Monthly",
                    isSelected = selectedFilter == 2,
                    onClick = { selectedFilter = 2 }
                )
                FilterChip(
                    text = "Overall",
                    isSelected = selectedFilter == 3,
                    onClick = { selectedFilter = 3 }
                )
            }




            LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                when (selectedFilter) {
                    0 -> {
                        val today = LocalDate.now().dayOfWeek.value-1
                        items(tasks.filter { it.days.get(today) ==  true }) { task ->
                            TaskCard(task, onUpdatePage = {
                                onTaskClick(task.id)
                            })

                        }
                    }

                    1 -> {
                        items(tasks.filter { it.repeat == 2 }) { task ->
                            TaskCard(task, onUpdatePage = {
                                onTaskClick(task.id)
                            })

                        }
                    }

                    2 -> {
                        items(tasks.filter { it.repeat == 3 }) { task ->
                            TaskCard(task, onUpdatePage = {
                                onTaskClick(task.id)
                            })

                        }
                    }

                    3 -> {
                        items(tasks) { task ->
                            TaskCard(task, onUpdatePage = {
                                onTaskClick(task.id)
                            })

                        }
                    }
                }
            }

        }
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize().padding(30.dp)
        ) {
            FloatingActionButton(
                onClick = toAddTaskPageClick,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить")
            }
        }
    }
}

@Preview()
@Composable
fun MainPagePreview() {
    HabbitAppTheme {
        MainPage(
             toAddTaskPageClick = {}, onTaskClick = {}, toAimsAndObjectivesPageClick = {}
        )
    }
}
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) Color.Green.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = if (isSelected) Color.Green else Color.Unspecified
    )
}