package com.example.habbitapp.view.ui.page



import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.FilterChip
import com.example.habbitapp.model.entity.Reminder
import com.example.habbitapp.model.entity.Task
import com.example.habbitapp.model.utils.setTimeNotification
import com.example.habbitapp.view.ui.card.ColorCircleRow
import com.example.habbitapp.viewmodel.ReminderViewModel
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UpdateTaskPage(toMainPageClick: ()-> Unit,idTask: Int) {
    val colors = listOf(
        // Rose/Red tones
        Color(0xFFFF8A80),  // Коралловый
        Color(0xFFF48FB1),  // Розовый

        // Orange/Yellow tones
        Color(0xFFFFAB91),  // Персиковый
        Color(0xFFFFE082),  // Янтарный

        // Green tones
        Color(0xFFA5D6A7),  // Мятный
        Color(0xFF80CBC4),  // Бирюзовый

        // Blue tones
        Color(0xFF81D4FA),  // Небесный
        Color(0xFF9FA8DA),  // Индиго

        // Purple tones
        Color(0xFFCE93D8),  // Лавандовый
        Color(0xFFB39DDB),  // Сиреневый

        // Neutral tones
        Color(0xFFEEEEEE),  // Светлый
        Color(0xFFBDBDBD),  // Средний
        Color(0xFF757575),  // Тёмный
        Color(0xFF424242),  // Очень тёмный
    )
    var text by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFilterRepeat by remember { mutableIntStateOf(1) }
    var selectedBackgroundColor by remember { mutableStateOf(colors[0]) }
    var selectedFilterMonday by remember { mutableStateOf(true) }
    var selectedFilterTue by remember { mutableStateOf(true) }
    var selectedFilterWed by remember { mutableStateOf(true) }
    var selectedFilterThu by remember { mutableStateOf(true) }
    var selectedFilterFri by remember { mutableStateOf(true) }
    var selectedFilterSat by remember { mutableStateOf(true) }
    var selectedFilterSun by remember { mutableStateOf(true) }
    val checkedState = remember { mutableStateOf(true) }
    val viewModelTask: TaskViewModel = viewModel()
    val viewModelReminder: ReminderViewModel = viewModel()
    val coroutine = rememberCoroutineScope()
    var minute by remember { mutableIntStateOf(1) }
    var hour by remember { mutableIntStateOf(10) }
    val scope = rememberCoroutineScope()
    var openDialogIcon by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedIcon by remember { mutableStateOf("📚") }
    LaunchedEffect(idTask) {
        val task = viewModelTask.findByIdTask(idTask)
        val reminder = viewModelReminder.findByIdReminderTask(idTask)
        text = task!!.name
        description = task.description
        checkedState.value = reminder?.isEnabled ?: false
        selectedBackgroundColor = Color(task.backgroundColor)
        selectedFilterRepeat = task.repeat
        selectedFilterMonday = task.days.get(0)
        selectedFilterTue = task.days.get(1)
        selectedFilterWed = task.days.get(2)
        selectedFilterThu = task.days.get(3)
        selectedFilterFri = task.days.get(4)
        selectedFilterSat = task.days.get(5)
        selectedFilterSun = task.days.get(6)

        selectedIcon = task.icon
        reminder?.let {
            minute = reminder.minute
            hour = reminder.hour
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            IconButton(onClick = toMainPageClick) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }

            Text("Save", modifier = Modifier.clickable {
                scope.launch {
                    val taskCurrent = viewModelTask.findByIdTask(idTask)
                    val reminderCurrent = viewModelReminder.findByIdReminderTask(idTask)
                    val list = mutableListOf<Boolean>(
                        selectedFilterMonday,
                        selectedFilterTue,
                        selectedFilterWed,
                        selectedFilterThu,
                        selectedFilterFri,
                        selectedFilterSat,
                        selectedFilterSun
                    )
                    val task = Task(
                        idTask,
                        text,
                        taskCurrent?.streak ?: 0,
                        selectedIcon,
                        description,
                        selectedBackgroundColor.toArgb(),
                        taskCurrent?.checkExec ?: false,
                        selectedFilterRepeat,
                        list
                    )
                    viewModelTask.updateTask(task)

                    if (checkedState.value) {
                        if (reminderCurrent != null) {


                            val reminder = Reminder(
                                id = reminderCurrent.id,
                                habitId = idTask,
                                hour = hour,
                                minute = minute,
                                isEnabled = true,
                                daysOfWeek = list
                            )
                            viewModelReminder.updateReminder(reminder)
                            setTimeNotification(
                                context,
                                text,
                                description,
                                hour,
                                minute,
                                list,
                                idTask
                            )
                        }else{
                            val reminder = Reminder(
                                id = 0,
                                habitId = idTask,
                                hour = hour,
                                minute = minute,
                                isEnabled = true,
                                daysOfWeek = list
                            )
                            viewModelReminder.insertReminder(reminder)
                            setTimeNotification(context, text, description,hour,minute,list,idTask)
                        }
                    }
                    toMainPageClick()
                }})


        }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    selectedIcon,
                    fontSize = 50.sp,
                    modifier = Modifier.clickable { openDialogIcon = true })
                if (openDialogIcon) FullScreenDialog({ openDialogIcon = false }, { icon ->
                    selectedIcon = icon
                    openDialogIcon = false
                })
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Habit Name") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                    )
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Add description") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier.width(200.dp), singleLine = true, maxLines = 1
                )

            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            /**
             *Список цветов
             */

            ColorCircleRow(colors, selectedBackgroundColor, onColorSelected = { color ->
                selectedBackgroundColor = color
            })
            Text("Repeat", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(60.dp)
            ) {
                FilterChip("Daily", selectedFilterRepeat == 1, { selectedFilterRepeat = 1 })
                FilterChip("Weekly", selectedFilterRepeat == 2, { selectedFilterRepeat = 2 })
                FilterChip("Monthly", selectedFilterRepeat == 3, { selectedFilterRepeat = 3 })
            }
            Text("On these days", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                FilterChipDays(
                    "Mon",
                    selectedFilterMonday,
                    { selectedFilterMonday = !selectedFilterMonday })
                FilterChipDays("Tue", selectedFilterTue, { selectedFilterTue = !selectedFilterTue })
                FilterChipDays("Wed", selectedFilterWed, { selectedFilterWed = !selectedFilterWed })
                FilterChipDays("Thu", selectedFilterThu, { selectedFilterThu = !selectedFilterThu })
                FilterChipDays("Fri", selectedFilterFri, { selectedFilterFri = !selectedFilterFri })
                FilterChipDays("Sat", selectedFilterSat, { selectedFilterSat = !selectedFilterSat })
                FilterChipDays("Sun", selectedFilterSun, { selectedFilterSun = !selectedFilterSun })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("Reminder", fontWeight = FontWeight.Bold)
                if (checkedState.value) {
                    TimePickerWithDialog(
                        onTimeSelected = { selectedHour, selectedMinute ->
                            hour = selectedHour
                            minute = selectedMinute
                        },
                        initialHour = hour,
                        initialMinute = minute

                    )
                }
                Switch(
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("Delete", fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    coroutine.launch {
                        viewModelTask.deleteByIdTask(idTask)
                    }
                    toMainPageClick()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "")
                }
            }

        }
    }


@Composable
@Preview
fun UpdateTaskPagePreview() {

    UpdateTaskPage({}, 0)
}


