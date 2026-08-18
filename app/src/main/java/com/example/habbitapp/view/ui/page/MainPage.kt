package com.example.habbitapp.view.ui.page

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalCursorBlinkEnabled
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.model.utils.StreakManager
import com.example.habbitapp.model.entity.Task
import com.example.habbitapp.model.utils.DailySummaryReceiver
import com.example.habbitapp.view.ui.card.TaskCard
import com.example.habbitapp.view.ui.scaffold.AppDrawerScaffold
import com.example.habbitapp.view.ui.state.ScreenStateContent
import com.example.habbitapp.view.ui.state.UiState
import com.example.habbitapp.view.ui.theme.HabbitAppTheme
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


@SuppressLint("NewApi")
@Composable
fun MainPage(
    toAddTaskPageClick: () -> Unit,
    onTaskClick: (Int) -> Unit,
    toAimsAndObjectivesPageClick: () -> Unit,
    toSettingsPage: () -> Unit,
    toProductivityPage: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val viewModel: TaskViewModel = viewModel()
    val tasksState by viewModel.tasksState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(tasksState) {
        val tasks = (tasksState as? UiState.Success)?.data ?: return@LaunchedEffect
        tasks.forEach { task ->
            val resetTask = StreakManager.resetCheckExecForNewDay(task)
            if (resetTask != task) {
                viewModel.updateTask(resetTask)
            }
        }
    }
    AppDrawerScaffold(
        toAimsAndObjectivesPageClick = toAimsAndObjectivesPageClick,
        toSettingsPage = toSettingsPage,
        toProductivityPage = toProductivityPage,
        toMainPageClick = {
            scope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        },
        drawerState = drawerState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
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
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.title_habit),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        stringResource(R.string.title_app),
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
                    text = stringResource(R.string.filter_today),
                    isSelected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 }
                )
                FilterChip(
                    text = stringResource(R.string.filter_weekly),
                    isSelected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 }
                )
                FilterChip(
                    text = stringResource(R.string.filter_monthly),
                    isSelected = selectedFilter == 2,
                    onClick = { selectedFilter = 2 }
                )

                FilterChip(
                    text = stringResource(R.string.filter_overall),
                    isSelected = selectedFilter == 3,
                    onClick = { selectedFilter = 3 }

                )
            }
            ScreenStateContent(
                state = tasksState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                onRetry = { viewModel.reloadTasks() },
            ) { tasks ->
                HabitList(
                    tasks = tasks,
                    selectedFilter = selectedFilter,
                    onTaskClick = onTaskClick,
                    onTaskUpdate = { viewModel.updateTask(it) },
                )
            }

        }
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            FloatingActionButton(
                onClick = toAddTaskPageClick,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_add))
            }
        }
    }

}

@SuppressLint("NewApi")
@Composable
 fun HabitList(
    tasks: List<Task>,
    selectedFilter: Int,
    onTaskClick: (Int) -> Unit,
    onTaskUpdate: (Task) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val filtered = when (selectedFilter) {
            0 -> {
                val today = LocalDate.now().dayOfWeek.value - 1
                tasks.filter { it.days.getOrNull(today) == true }
            }
            1 -> tasks.filter { it.repeat == 2 }
            2 -> tasks.filter { it.repeat == 3 }
            else -> tasks
        }
        items(filtered, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onUpdatePage = { onTaskClick(task.id) },
                onTaskUpdate = onTaskUpdate,
            )
        }
    }
}

@Preview()
@Composable
fun MainPagePreview() {
    HabbitAppTheme {
        MainPage(
            toAddTaskPageClick = {}, onTaskClick = {}, toAimsAndObjectivesPageClick = {},
            toSettingsPage = {},
            toProductivityPage = {}
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

