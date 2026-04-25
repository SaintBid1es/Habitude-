package com.example.habbitapp.ui.page

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.AlarmReceiver
import com.example.habbitapp.FilterChip
import com.example.habbitapp.R
import com.example.habbitapp.entity.Reminder
import com.example.habbitapp.entity.Task
import com.example.habbitapp.setTimeNotification
import com.example.habbitapp.ui.card.ColorCircleRow
import com.example.habbitapp.viewmodel.ReminderViewModel
import com.example.habbitapp.viewmodel.ReminderViewModel.Companion.NOTIFICATION_ID
import com.example.habbitapp.viewmodel.ReminderViewModel.Companion.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskPage(toMainPageClick: ()-> Unit) {
    val colors = listOf(

        Color(0xFFFF8A80),
        Color(0xFFF48FB1),

        Color(0xFFFFAB91),
        Color(0xFFFFE082),

        Color(0xFFA5D6A7),
        Color(0xFF80CBC4),


        Color(0xFF81D4FA),
        Color(0xFF9FA8DA),
        Color(0xFFCE93D8),
        Color(0xFFB39DDB),

        Color(0xFFEEEEEE),
        Color(0xFFBDBDBD),
        Color(0xFF757575),
        Color(0xFF424242),
    )
    var text by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFilterRepeat by remember { mutableIntStateOf(1) }

    var selectedFilterMonday by remember { mutableStateOf(true) }
    var selectedFilterTue by remember { mutableStateOf(true) }
    var selectedFilterWed by remember { mutableStateOf(true) }
    var selectedFilterThu by remember { mutableStateOf(true) }
    var selectedFilterFri by remember { mutableStateOf(true) }
    var selectedFilterSat by remember { mutableStateOf(true) }
    var selectedFilterSun by remember { mutableStateOf(true) }

    var selectedBackgroundColor by remember { mutableStateOf(colors[0]) }
    val viewModelTask : TaskViewModel = viewModel()
    val viewModelReminder : ReminderViewModel = viewModel()
    val checkedState = remember { mutableStateOf(false) }

    var minute by remember { mutableIntStateOf(1) }
    var hour by remember { mutableIntStateOf(10) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var openDialogIcon by remember {mutableStateOf(false)}
    val activity = LocalActivity.current
    var selectedIcon by remember { mutableStateOf("📚") }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("AddTaskPage", "Notification permission granted")
        } else {
            android.util.Log.d("AddTaskPage", "Notification permission denied")
        }
    }


    Column(modifier = Modifier
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
            Text("Save", modifier = Modifier.clickable{
                scope.launch {


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
                        0,
                        text,
                        0,
                        selectedIcon,
                        description,
                        selectedBackgroundColor.toArgb(),
                        false,
                        selectedFilterRepeat,
                        list
                    )
                    val habitId = viewModelTask.insertTaskAndGetId(task)
                    if (checkedState.value) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val hasPermission = activity?.let {
                                ActivityCompat.checkSelfPermission(
                                    it,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            } ?: false

                            if (!hasPermission) {

                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                                kotlinx.coroutines.delay(500)


                                val hasPermissionAfter = activity?.let {
                                    ActivityCompat.checkSelfPermission(
                                        it,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                } ?: false

                                if (!hasPermissionAfter) {
                                    android.util.Log.e("AddTaskPage", "Cannot set reminder: no notification permission")
                                    toMainPageClick()
                                    return@launch
                                }
                            }
                        }
                        val reminder = Reminder(
                            id = 0,
                            habitId = habitId.toInt(),
                            hour = hour,
                            minute = minute,
                            isEnabled = true,
                            daysOfWeek = list
                        )
                        viewModelReminder.insertReminder(reminder)
                        setTimeNotification(context, text, description,hour,minute,list,habitId.toInt())
                    }




                    toMainPageClick()
                }
            })

        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(selectedIcon, fontSize = 50.sp, modifier = Modifier.clickable{openDialogIcon = true})
            if (openDialogIcon) FullScreenDialog({openDialogIcon = false},{
                icon->selectedIcon = icon
            openDialogIcon = false})
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
                modifier = Modifier.width(200.dp)
            )

        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        /**
         *Список цветов
         */

        ColorCircleRow(colors,selectedBackgroundColor, onColorSelected = {
             color ->
                selectedBackgroundColor = color
        })
        Text("Repeat", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(60.dp)) {
            FilterChip("Daily",selectedFilterRepeat==1,{selectedFilterRepeat=1})
            FilterChip("Weekly",selectedFilterRepeat==2,{selectedFilterRepeat=2})
            FilterChip("Monthly",selectedFilterRepeat==3,{selectedFilterRepeat=3})
        }
        Text("On these days", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            FilterChipDays("Mon",selectedFilterMonday, { selectedFilterMonday=!selectedFilterMonday })
            FilterChipDays("Tue",selectedFilterTue, { selectedFilterTue=!selectedFilterTue })
            FilterChipDays("Wed",selectedFilterWed, { selectedFilterWed=!selectedFilterWed })
            FilterChipDays("Thu",selectedFilterThu, { selectedFilterThu=!selectedFilterThu })
            FilterChipDays("Fri",selectedFilterFri, { selectedFilterFri=!selectedFilterFri })
            FilterChipDays("Sat",selectedFilterSat, { selectedFilterSat=!selectedFilterSat })
            FilterChipDays("Sun",selectedFilterSun, { selectedFilterSun=!selectedFilterSun })
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)) {
            Text("Reminder", fontWeight = FontWeight.Bold)
            if (checkedState.value){
                TimePickerWithDialog(onTimeSelected = { selectedHour, selectedMinute ->
                    hour = selectedHour
                    minute = selectedMinute
                })
            }
            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )

        }



    }
}


@Composable
@Preview
fun AddTaskPagePreview() {
    AddTaskPage({})
}
@Composable
fun FilterChipDays(
    text: String,
    isSelected: Boolean,
    onClick:()-> Unit
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWithDialog(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    initialHour: Int = 8,
    initialMinute: Int = 0,
    modifier: Modifier = Modifier,
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var showDialog by remember { mutableStateOf(false) }

    val formattedTime = remember(selectedHour, selectedMinute) {
        String.format("%02d:%02d", selectedHour, selectedMinute)
    }

    LaunchedEffect(initialHour, initialMinute) {
        selectedHour = initialHour
        selectedMinute = initialMinute
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clickable { showDialog = true }
                .background(
                    color = Color(0xFF4A4E69).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = formattedTime,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }


        if (showDialog) {
            val timeState = rememberTimePickerState(
                is24Hour = true,
                initialHour = selectedHour,
                initialMinute = selectedMinute
            )

            Dialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = true)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF2D2D44),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Выберите время",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        TimePicker(
                            state = timeState,
                            layoutType = TimePickerLayoutType.Vertical,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Gray
                                )
                            ) {
                                Text("Отмена")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    selectedHour = timeState.hour
                                    selectedMinute = timeState.minute
                                    onTimeSelected(timeState.hour, timeState.minute)
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Сохранить")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenDialog(onDismissRequest: () -> Unit, onIconSelected: (String) -> Unit) {
    val scrollState = rememberScrollState()
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(text = "🎨", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Choose Your Icon",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                CategoryHeader(emoji = "🏃‍♂️", title = "Sports & Activity")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(listOf(
                        "🏃‍♂️", "🚴‍♂️", "🏊‍♂️", "🏋️‍♂️", "🧘‍♀️", "🚶‍♀️", "🧗‍♂️", "🤸‍♀️",
                        "⚽", "🏀", "🎾", "🏈", "⚾", "🥋", "🥊", "🎯"
                    )) { emoji ->
                        IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                    }
                }

                CategoryHeader(emoji = "🥗", title = "Food & Health")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(listOf(
                        "🥗", "🍎", "🥑", "🥦", "💪", "🫀", "🧠", "🦷",
                        "💧", "🍵", "🥤", "🍌", "🥕", "🍳", "🥩", "🍝"
                    )) { emoji ->
                        IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                    }
                }

                CategoryHeader(emoji = "📚", title = "Work & Study")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(listOf(
                        "📚", "💻", "📝", "✏️", "📖", "🧠", "🎓", "💼",
                        "📊", "📈", "📉", "🔬", "🎨", "🎭", "🎹", "📷"
                    )) { emoji ->
                        IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                    }
                }

                CategoryHeader(emoji = "🏠", title = "Home & Self Care")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(listOf(
                        "🏠", "🧹", "🧺", "🚿", "🛌", "🧴", "🪴", "🐱",
                        "🐶", "🎮", "📺", "🎬", "🎵", "🧩", "🎲", "🧸"
                    )) { emoji ->
                        IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                    }
                }

                CategoryHeader(emoji = "💰", title = "Finance & Goals")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(listOf(
                        "💰", "💎", "🏦", "📈", "💳", "🎯", "🏆", "⭐",
                        "🔥", "💫", "✨", "🌟", "⚡", "💡", "🔑", "🗝️"
                    )) { emoji ->
                        IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                    }
                }
                CategoryHeader(emoji = "✈️", title = "Travel & Adventure")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)) {
                items(listOf(
                    "✈️", "🌍", "🗺️", "🧭", "🏔️", "🏖️", "🌲", "🏕️",
                    "🚗", "🚲", "🚌", "⛴️", "🎒", "📸", "🌅", "⛺"
                )) { emoji ->
                    IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                }
            }

                CategoryHeader(emoji = "🎨", title = "Creativity")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)) {
                items(listOf(
                    "🎨", "✍️", "📸", "🎵", "🎸", "🎹", "🎭", "💃",
                    "🖌️", "📓", "🧵", "🎬", "📻", "🎧", "🎤", "🪄"
                )) { emoji ->
                    IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                }
            }

                CategoryHeader(emoji = "📱", title = "Technology")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)) {
                items(listOf(
                    "📱", "💻", "🖥️", "⌚", "🎮", "🤖", "💾", "🔌",
                    "📡", "🎥", "📹", "🔋", "💡", "⚙️", "🔧", "🧰"
                )) { emoji ->
                    IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                }
            }

                CategoryHeader(emoji = "😊", title = "Mood & Emotions")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(listOf(
                        "😊", "🥰", "😎", "🤗", "😌", "💪", "🧘", "🌞",
                        "🌈", "🌸", "🌺", "🍀", "🦋", "🐬", "🕊️", "🌙"
                    )) { emoji ->
                        IconCircle(emoji = emoji, onClick = { onIconSelected(emoji) })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16213E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(emoji: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun IconCircle(emoji: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                Color.White
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 28.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}
@Composable
@Preview
fun FullScreenDialogPreview(){
    FullScreenDialog({},{""})
}