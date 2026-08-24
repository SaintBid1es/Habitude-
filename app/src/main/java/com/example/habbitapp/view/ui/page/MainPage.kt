package com.example.habbitapp.view.ui.page

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.model.entity.FailedHabitWithTask
import com.example.habbitapp.model.entity.Task
import com.example.habbitapp.model.utils.FailedHabitManager
import com.example.habbitapp.view.ui.card.TaskCard
import com.example.habbitapp.view.ui.scaffold.AppDrawerScaffold
import com.example.habbitapp.view.ui.state.ScreenStateContent
import com.example.habbitapp.view.ui.state.UiState
import com.example.habbitapp.view.ui.theme.HabbitAppTheme
import com.example.habbitapp.viewmodel.FailedHabitViewModel
import com.example.habbitapp.viewmodel.TaskViewModel
import com.example.habbitapp.viewmodel.ViewModelProvider.FailedHabitViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


/**
 * Причина, по которой пользователь пропустил привычку.
 */
enum class FailedHabitReason {
    NOT_POWER,
    NOT_TIME,
    FORGOT,
    OTHER
}


/**
 * Route главной страницы.
 *
 * Здесь находится логика:
 * - Context
 * - Room
 * - ViewModel
 *
 * MainPage остаётся чистым UI.
 */
@SuppressLint("NewApi")
@Composable
fun MainPageRoute(
    toAddTaskPageClick: () -> Unit,
    onTaskClick: (Int) -> Unit,
    toAimsAndObjectivesPageClick: () -> Unit,
    toSettingsPage: () -> Unit,
    toProductivityPage: () -> Unit
) {

    /*
     * ВАЖНО:
     *
     * Используется Compose LocalContext,
     * НЕ androidx.glance.LocalContext.
     */
    val context = LocalContext.current

    val database = remember(context) {
        ItemDatabase.getInstance(context)
    }

    /*
     * ViewModel привычек.
     */
    val taskViewModel: TaskViewModel = viewModel()

    /*
     * Factory для FailedHabitViewModel.
     */
    val factory = remember(database) {

        FailedHabitViewModelFactory(
            failedHabitDao =
                database.failedHabitDao(),

            statFailHabitDao =
                database.statFailHabitDao()
        )
    }

    val failedHabitViewModel: FailedHabitViewModel =
        viewModel(
            factory = factory
        )


    /*
     * ==========================================
     * ПРОВЕРКА ПРОПУЩЕННЫХ ПРИВЫЧЕК
     * ==========================================
     *
     * При открытии главной страницы проверяем,
     * была ли вчера какая-нибудь привычка
     * запланирована, но не выполнена.
     */
    LaunchedEffect(Unit) {

        withContext(Dispatchers.IO) {

            val tasks =
                database
                    .taskDao()
                    .getAllTasksBlocking()

            FailedHabitManager.checkMissedHabits(
                context = context,
                tasks = tasks
            )
        }
    }


    /*
     * ==========================================
     * Состояние привычек
     * ==========================================
     */
    val tasksState by
    taskViewModel.tasksState
        .collectAsStateWithLifecycle()


    /*
     * ==========================================
     * Количество необработанных пропусков
     * ==========================================
     */
    val unansweredCount by
    failedHabitViewModel.unansweredCount
        .collectAsStateWithLifecycle()


    /*
     * ==========================================
     * Необработанные пропуски
     *
     * Теперь здесь FailedHabitWithTask,
     * поэтому у нас сразу есть:
     *
     * - название привычки
     * - иконка
     * - дата
     * - taskId
     * - id FailedHabit
     * ==========================================
     */
    val unanswered by
    failedHabitViewModel.unanswered
        .collectAsStateWithLifecycle()


    /*
     * Берём первый пропуск.
     *
     * Пока пользователь отвечает на причины
     * последовательно.
     */
    val firstUnansweredHabit =
        unanswered.firstOrNull()


    /*
     * ==========================================
     * Передаём всё в чистый UI
     * ==========================================
     */
    MainPage(

        tasksState =
            tasksState,

        unansweredCount =
            unansweredCount,

        firstUnansweredHabit =
            firstUnansweredHabit,

        toAddTaskPageClick =
            toAddTaskPageClick,

        onTaskClick =
            onTaskClick,

        toAimsAndObjectivesPageClick =
            toAimsAndObjectivesPageClick,

        toSettingsPage =
            toSettingsPage,

        toProductivityPage =
            toProductivityPage,

        onReasonSelected = { failedHabit, reason ->

            failedHabitViewModel.markReasonSelected(
                failedHabitId =
                    failedHabit.failedHabitId,

                reason =
                    reason
            )
        },

        onRetry = {

            taskViewModel.reloadTasks()
        },

        onTaskUpdate = { task ->

            taskViewModel.updateTask(task)
        }
    )
}


/**
 * Главный UI.
 *
 * Здесь НЕТ:
 * - ViewModel
 * - Room
 * - Context
 * - DAO
 *
 * Поэтому Preview работает.
 */
@SuppressLint("NewApi")
@Composable
fun MainPage(
    tasksState: UiState<List<Task>>,

    unansweredCount: Int,

    firstUnansweredHabit: FailedHabitWithTask?,

    toAddTaskPageClick: () -> Unit,

    onTaskClick: (Int) -> Unit,

    toAimsAndObjectivesPageClick: () -> Unit,

    toSettingsPage: () -> Unit,

    toProductivityPage: () -> Unit,

    onReasonSelected: (
        failedHabit: FailedHabitWithTask,
        reason: FailedHabitReason
    ) -> Unit,

    onRetry: () -> Unit,

    onTaskUpdate: (Task) -> Unit
) {

    /*
     * ==========================================
     * FILTER
     * ==========================================
     */
    var selectedFilter by remember {
        mutableIntStateOf(0)
    }


    /*
     * ==========================================
     * DIALOG
     * ==========================================
     */
    var showFailedHabitDialog by remember {
        mutableStateOf(false)
    }


    /*
     * ==========================================
     * DRAWER
     * ==========================================
     */
    val drawerState = rememberDrawerState(
        initialValue =
            DrawerValue.Closed
    )

    val scope =
        rememberCoroutineScope()


    /*
     * ==========================================
     * ДИАЛОГ ПРОПУЩЕННОЙ ПРИВЫЧКИ
     * ==========================================
     */
    if (
        showFailedHabitDialog &&
        firstUnansweredHabit != null
    ) {

        FailedHabitReasonDialog(

            failedHabit =
                firstUnansweredHabit,

            onDismiss = {

                showFailedHabitDialog =
                    false
            },

            onReasonSelected = { reason ->

                onReasonSelected(
                    firstUnansweredHabit,
                    reason
                )

                /*
                 * Закрываем диалог.
                 *
                 * После изменения Room:
                 *
                 * unanswered
                 * ↓
                 * Flow
                 * ↓
                 * unansweredCount
                 * ↓
                 * badge уменьшается.
                 */
                showFailedHabitDialog =
                    false
            }
        )
    }


    /*
     * ==========================================
     * DRAWER SCAFFOLD
     * ==========================================
     */
    AppDrawerScaffold(

        toAimsAndObjectivesPageClick =
            toAimsAndObjectivesPageClick,

        toSettingsPage =
            toSettingsPage,

        toProductivityPage =
            toProductivityPage,

        toMainPageClick = {

            scope.launch {

                if (drawerState.isClosed) {

                    drawerState.open()

                } else {

                    drawerState.close()
                }
            }
        },

        drawerState =
            drawerState

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(15.dp)

        ) {


            /*
             * ==========================================
             * HEADER
             * ==========================================
             */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {


                /*
                 * MENU
                 */
                IconButton(

                    onClick = {

                        scope.launch {
                            drawerState.open()
                        }
                    }

                ) {

                    Icon(

                        imageVector =
                            Icons.Filled.Menu,

                        contentDescription =
                            stringResource(
                                R.string.cd_menu
                            )
                    )
                }


                /*
                 * TITLE
                 */
                Row(

                    modifier =
                        Modifier.weight(1f),

                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(

                        text =
                            stringResource(
                                R.string.title_habit
                            ),

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            20.sp
                    )


                    Text(

                        text =
                            stringResource(
                                R.string.title_app
                            ),

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            20.sp,

                        modifier =
                            Modifier.padding(
                                start = 10.dp
                            ),

                        color =
                            Color.Green
                    )
                }


                /*
                 * ==========================================
                 * NOTIFICATIONS
                 * ==========================================
                 *
                 * Badge показывает количество
                 * пропущенных привычек,
                 * для которых пользователь ещё
                 * не указал причину.
                 */
                BadgedBox(

                    badge = {

                        if (unansweredCount > 0) {

                            Badge(

                                containerColor =
                                    Color.Red,

                                contentColor =
                                    Color.White

                            ) {

                                Text(
                                    text =
                                        unansweredCount
                                            .toString()
                                )
                            }
                        }
                    }

                ) {

                    IconButton(

                        onClick = {

                            /*
                             * Открываем диалог только
                             * если действительно есть
                             * необработанный пропуск.
                             */
                            if (
                                unansweredCount > 0 &&
                                firstUnansweredHabit != null
                            ) {

                                showFailedHabitDialog =
                                    true
                            }
                        }

                    ) {

                        Icon(

                            imageVector =
                                Icons.Filled.Notifications,

                            contentDescription =
                                "Пропущенные привычки"
                        )
                    }
                }
            }


            /*
             * ==========================================
             * FILTERS
             * ==========================================
             */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceAround

            ) {

                FilterChip(

                    text =
                        stringResource(
                            R.string.filter_today
                        ),

                    isSelected =
                        selectedFilter == 0,

                    onClick = {

                        selectedFilter = 0
                    }
                )


                FilterChip(

                    text =
                        stringResource(
                            R.string.filter_weekly
                        ),

                    isSelected =
                        selectedFilter == 1,

                    onClick = {

                        selectedFilter = 1
                    }
                )


                FilterChip(

                    text =
                        stringResource(
                            R.string.filter_monthly
                        ),

                    isSelected =
                        selectedFilter == 2,

                    onClick = {

                        selectedFilter = 2
                    }
                )


                FilterChip(

                    text =
                        stringResource(
                            R.string.filter_overall
                        ),

                    isSelected =
                        selectedFilter == 3,

                    onClick = {

                        selectedFilter = 3
                    }
                )
            }


            /*
             * ==========================================
             * TASKS
             * ==========================================
             */

            ScreenStateContent(

                state =
                    tasksState,

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp),

                onRetry =
                    onRetry

            ) { tasks ->

                HabitList(

                    tasks =
                        tasks,

                    selectedFilter =
                        selectedFilter,

                    onTaskClick =
                        onTaskClick,

                    onTaskUpdate =
                        onTaskUpdate
                )
            }
        }


        /*
         * ==========================================
         * ADD BUTTON
         * ==========================================
         */

        Box(

            contentAlignment =
                Alignment.BottomEnd,

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(30.dp)

        ) {

            FloatingActionButton(

                onClick =
                    toAddTaskPageClick

            ) {

                Icon(

                    imageVector =
                        Icons.Filled.Add,

                    contentDescription =
                        stringResource(
                            R.string.cd_add
                        )
                )
            }
        }
    }
}


/**
 * ==========================================
 * ДИАЛОГ ПРИЧИНЫ ПРОПУСКА
 * ==========================================
 */
@Composable
private fun FailedHabitReasonDialog(

    failedHabit:
    FailedHabitWithTask,

    onDismiss:
        () -> Unit,

    onReasonSelected:
        (FailedHabitReason) -> Unit

) {

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                text =
                    "Пропущена привычка"
            )
        },

        text = {

            Column(

                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {


                /*
                 * ==========================================
                 * НАЗВАНИЕ ПРИВЫЧКИ
                 * ==========================================
                 */
                Text(

                    text =
                        "${failedHabit.taskIcon} ${failedHabit.taskName}",

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        18.sp
                )


                /*
                 * Дата пропуска
                 */
                Text(

                    text =
                        "Дата: ${failedHabit.date}",

                    fontSize =
                        14.sp,

                    color =
                        Color.Gray
                )


                /*
                 * Вопрос
                 */
                Text(

                    text =
                        "Почему не получилось выполнить?",

                    fontSize =
                        16.sp
                )


                /*
                 * ==========================================
                 * ПРИЧИНЫ
                 * ==========================================
                 */

                ReasonItem(

                    text =
                        stringResource(
                            R.string.notPower
                        ),

                    onClick = {

                        onReasonSelected(
                            FailedHabitReason.NOT_POWER
                        )
                    }
                )


                ReasonItem(

                    text =
                        stringResource(
                            R.string.notTime
                        ),

                    onClick = {

                        onReasonSelected(
                            FailedHabitReason.NOT_TIME
                        )
                    }
                )


                ReasonItem(

                    text =
                        stringResource(
                            R.string.forgot
                        ),

                    onClick = {

                        onReasonSelected(
                            FailedHabitReason.FORGOT
                        )
                    }
                )


                ReasonItem(

                    text =
                        stringResource(
                            R.string.other
                        ),

                    onClick = {

                        onReasonSelected(
                            FailedHabitReason.OTHER
                        )
                    }
                )
            }
        },

        confirmButton = {}
    )
}


/**
 * Элемент причины.
 */
@Composable
private fun ReasonItem(

    text: String,

    onClick: () -> Unit

) {

    Text(

        text =
            text,

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(8.dp),

        fontSize =
            16.sp
    )
}


/**
 * ==========================================
 * СПИСОК ПРИВЫЧЕК
 * ==========================================
 */
@SuppressLint("NewApi")
@Composable
fun HabitList(

    tasks: List<Task>,

    selectedFilter: Int,

    onTaskClick:
        (Int) -> Unit,

    onTaskUpdate:
        (Task) -> Unit

) {

    val filtered = when (selectedFilter) {


        /*
         * Сегодня
         */
        0 -> {

            val today =
                LocalDate.now()
                    .dayOfWeek
                    .value - 1

            tasks.filter {

                it.days.getOrNull(
                    today
                ) == true
            }
        }


        /*
         * Неделя
         */
        1 -> {

            tasks.filter {

                it.repeat == 2
            }
        }


        /*
         * Месяц
         */
        2 -> {

            tasks.filter {

                it.repeat == 3
            }
        }


        /*
         * Все
         */
        else -> {

            tasks
        }
    }


    LazyColumn(

        modifier =
            Modifier.fillMaxSize()

    ) {

        items(

            items =
                filtered,

            key = {
                it.id
            }

        ) { task ->

            TaskCard(

                task =
                    task,

                onUpdatePage = {

                    onTaskClick(
                        task.id
                    )
                },

                onTaskUpdate =
                    onTaskUpdate
            )
        }
    }
}


/**
 * ==========================================
 * FILTER CHIP
 * ==========================================
 */
@Composable
fun FilterChip(

    text: String,

    isSelected: Boolean,

    onClick: () -> Unit

) {

    Text(

        text =
            text,

        fontWeight =
            FontWeight.Bold,

        fontSize =
            16.sp,

        modifier =
            Modifier
                .clickable {
                    onClick()
                }
                .background(

                    color =
                        if (isSelected) {

                            Color.Green.copy(
                                alpha = 0.2f
                            )

                        } else {

                            Color.Transparent
                        },

                    shape =
                        RoundedCornerShape(
                            8.dp
                        )
                )
                .padding(

                    horizontal =
                        12.dp,

                    vertical =
                        6.dp
                ),

        color =
            if (isSelected) {

                Color.Green

            } else {

                Color.Unspecified
            }
    )
}


/**
 * ==========================================
 * PREVIEW
 * ==========================================
 */
@SuppressLint("NewApi")
@Preview(

    showBackground =
        true,

    showSystemUi =
        true
)
@Composable
fun MainPagePreview() {

    HabbitAppTheme {

        MainPage(

            tasksState =
                UiState.Success(
                    data =
                        previewTasks
                ),

            unansweredCount =
                2,

            /*
             * В Preview можно оставить null,
             * потому что мы не хотим запускать
             * реальный Room.
             */
            firstUnansweredHabit =
                null,

            toAddTaskPageClick =
                {},

            onTaskClick =
                {},

            toAimsAndObjectivesPageClick =
                {},

            toSettingsPage =
                {},

            toProductivityPage =
                {},

            onReasonSelected =
                { _, _ -> },

            onRetry =
                {},

            onTaskUpdate =
                {}
        )
    }
}


/**
 * ==========================================
 * PREVIEW TASKS
 * ==========================================
 */
private val previewTasks =
    listOf(

        Task(

            id =
                1,

            name =
                "Утренняя зарядка",

            streak =
                7,

            icon =
                "🏃",

            description =
                "10 минут упражнений",

            backgroundColor =
                Color(0xFFE8F5E9)
                    .value
                    .toInt(),

            checkExec =
                false,

            repeat =
                2,

            days =
                listOf(
                    true,
                    true,
                    true,
                    true,
                    true,
                    false,
                    false
                ),

            completionDates =
                emptyList()
        ),


        Task(

            id =
                2,

            name =
                "Читать книгу",

            streak =
                12,

            icon =
                "📚",

            description =
                "Прочитать минимум 20 страниц",

            backgroundColor =
                Color(0xFFE3F2FD)
                    .value
                    .toInt(),

            checkExec =
                true,

            repeat =
                2,

            days =
                listOf(
                    true,
                    false,
                    true,
                    false,
                    true,
                    false,
                    false
                ),

            completionDates =
                emptyList()
        ),


        Task(

            id =
                3,

            name =
                "Изучать Kotlin",

            streak =
                5,

            icon =
                "💻",

            description =
                "Изучить Jetpack Compose",

            backgroundColor =
                Color(0xFFFFF3E0)
                    .value
                    .toInt(),

            checkExec =
                false,

            repeat =
                3,

            days =
                listOf(
                    true,
                    true,
                    false,
                    true,
                    false,
                    true,
                    false
                ),

            completionDates =
                emptyList()
        )
    )