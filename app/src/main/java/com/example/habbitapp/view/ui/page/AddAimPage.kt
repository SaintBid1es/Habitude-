package com.example.habbitapp.view.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.FilterChip
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.view.ui.theme.GreenPrimary
import com.example.habbitapp.viewmodel.AimViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAimPage(toAimsPageClick: () -> Unit) {

    var text by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("") }
    val viewModelAim: AimViewModel = viewModel()
    var openDialogPriority by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var onDialogWindow by remember { mutableStateOf(false) }
    var onDialogCalendar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val checkedState = remember { mutableStateOf(false) }
    var selectedDate = remember { mutableStateOf("Сегодня") }
    var selectedDate2 by remember { mutableStateOf<Long?>(null) }
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
            IconButton(onClick = toAimsPageClick) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }
            Text("Save", modifier = Modifier.clickable {
                scope.launch {

//                    val aim = Aims(
//                        0,
//                        text,
//                        selectedIcon,
//                        description,
//                        false,
//                        selectedCategory
//                    )
//                    viewModelAim.insertAim(aim)

                    toAimsPageClick()
                }
            })

        }
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                onDialogCalendar = true
            }) {
                    Icon(Icons.Default.DateRange, contentDescription = "")
            }
            var date = convertMillisToDate(selectedDate2 ?: System.currentTimeMillis())
            Text("${date}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            if (onDialogCalendar){
                DatePickerModal(
                    onDateSelected = { selectedDate2 = it },
                    onDismiss = { onDialogCalendar = false }
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("task name") },
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
                label = { Text("subtask") },
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
        Row {
            Text(
                "Category >  ", modifier = Modifier
                    .padding(10.dp)
                    .clickable { onDialogWindow = true }, fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontSize = 15.sp
            )
            Text(selectedCategory)
        }
        if (onDialogWindow) {
            AlertDialog(
                onDismissRequest = { onDialogWindow = false },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Нет", modifier = Modifier.clickable { onDialogWindow = false }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Покупки", modifier = Modifier.clickable {
                            selectedCategory = "Покупки"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Дом", modifier = Modifier.clickable {
                            selectedCategory = "Дом"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Работа", modifier = Modifier.clickable {
                            selectedCategory = "Работа"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Семья", modifier = Modifier.clickable {
                            selectedCategory = "Семья"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Здоровье", modifier = Modifier.clickable {
                            selectedCategory = "Здоровье"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Финансы", modifier = Modifier.clickable {
                            selectedCategory = "Финансы"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Развлечения", modifier = Modifier.clickable {
                            selectedCategory = "Развлечения"
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                       },
                confirmButton = {}
            )
        }
        Row {
            Text(
                "Priority >", modifier = Modifier
                    .padding(10.dp)
                    .clickable { openDialogPriority = true }, fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontSize = 15.sp
            )
            Text(selectedPriority)
        }
        if (openDialogPriority) {
            AlertDialog(
                onDismissRequest = { openDialogPriority = false },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Нет", modifier = Modifier.clickable {
                            selectedPriority = "Нет"
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                        Text("Низкий", modifier = Modifier.clickable {
                            selectedPriority = "Низкий"
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Средний", modifier = Modifier.clickable {
                            selectedPriority = "Средний"
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Высокий", modifier = Modifier.clickable {
                            selectedPriority = "Высокий"
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                },
                confirmButton = {}
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)) {
            Text("Automatic transfer", fontWeight = FontWeight.Bold,color = Color.Green, fontSize = 15.sp)
            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )
        }


    }
}


@Preview
@Composable
fun AddAimPagePreviw() {
    AddAimPage({})
}
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}