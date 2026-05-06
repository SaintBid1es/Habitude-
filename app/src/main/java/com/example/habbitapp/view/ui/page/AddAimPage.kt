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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.R
import com.example.habbitapp.viewmodel.AimViewModel
import io.github.chouaibmo.rowkalendar.extensions.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAimPage(toAimsPageClick: () -> Unit) {
    val noneCategory = stringResource(R.string.aim_none)
    val shoppingCategory = stringResource(R.string.aim_category_shopping)
    val homeCategory = stringResource(R.string.aim_category_home)
    val workCategory = stringResource(R.string.aim_category_work)
    val familyCategory = stringResource(R.string.aim_category_family)
    val healthCategory = stringResource(R.string.aim_category_health)
    val financeCategory = stringResource(R.string.aim_category_finance)
    val funCategory = stringResource(R.string.aim_category_fun)
    var text by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(noneCategory) }
    var selectedPriority by remember { mutableIntStateOf(0) }
    val viewModelAim: AimViewModel = viewModel()
    var openDialogPriority by remember { mutableStateOf(false) }
    var onDialogWindow by remember { mutableStateOf(false) }
    var onDialogCalendar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val checkedState = remember { mutableStateOf(false) }
    var selectedDate2 by remember { mutableStateOf<Long?>(null) }
    val date = convertMillisToDate(selectedDate2 ?: System.currentTimeMillis())
    //val date = LocalDate.now()

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
            Text(stringResource(R.string.action_save), modifier = Modifier.clickable {
                scope.launch {

                    val aim = Aims(
                        0,
                        text,
                        false,
                        selectedCategory,
                        selectedPriority,
                        checkedState.value,
                        null,
                        date.toString()
                    )
                    viewModelAim.insertAim(aim)

                    toAimsPageClick()
                }
            })

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onDialogCalendar = true
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "")
            }
            Text("${date}", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.clickable{
                onDialogCalendar = true
            })
            if (onDialogCalendar) {
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
                label = { Text(stringResource(R.string.label_task_name)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                )
            )
//            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//                TextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("subtask") },
//                    colors = TextFieldDefaults.colors(
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent,
//                        disabledContainerColor = Color.Transparent,
//                        errorContainerColor = Color.Transparent,
//                    ),
//                    modifier = Modifier.width(200.dp), singleLine = true, maxLines = 1
//
//                )
//                IconButton(onClick = {
//
//                    }
//
//                ) {
//                    Icon(
//                        Icons.Filled.Add,
//                        contentDescription = "",
//                    )
//                }
//            }

        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.aim_category), modifier = Modifier
                    .padding(10.dp)
                    .clickable { onDialogWindow = true }, fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontSize = 15.sp
            )
            Text(selectedCategory, fontWeight = FontWeight.Bold, modifier = Modifier.clickable{
                 onDialogWindow = true
            })
        }
        if (onDialogWindow) {
            AlertDialog(
                onDismissRequest = { onDialogWindow = false },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.aim_none),
                            modifier = Modifier.clickable {
                                selectedCategory = noneCategory
                                onDialogWindow = false
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(shoppingCategory, modifier = Modifier.clickable {
                            selectedCategory = shoppingCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(homeCategory, modifier = Modifier.clickable {
                            selectedCategory = homeCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(workCategory, modifier = Modifier.clickable {
                            selectedCategory = workCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(familyCategory, modifier = Modifier.clickable {
                            selectedCategory = familyCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(healthCategory, modifier = Modifier.clickable {
                            selectedCategory = healthCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(financeCategory, modifier = Modifier.clickable {
                            selectedCategory = financeCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(funCategory, modifier = Modifier.clickable {
                            selectedCategory = funCategory
                            onDialogWindow = false
                        }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                },
                confirmButton = {}
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.aim_priority), modifier = Modifier
                    .padding(10.dp)
                    .clickable { openDialogPriority = true }, fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontSize = 15.sp
            )
            when (selectedPriority) {
                0 -> Text(stringResource(R.string.aim_none), fontWeight = FontWeight.Bold, modifier = Modifier.clickable{  openDialogPriority = true })
                1 -> Text(stringResource(R.string.aim_low), fontWeight = FontWeight.Bold, modifier = Modifier.clickable{  openDialogPriority = true })
                2 -> Text(stringResource(R.string.aim_medium), fontWeight = FontWeight.Bold, modifier = Modifier.clickable{  openDialogPriority = true })
                3 -> Text(stringResource(R.string.aim_high), fontWeight = FontWeight.Bold, modifier = Modifier.clickable{  openDialogPriority = true })
            }

        }
        if (openDialogPriority) {
            AlertDialog(
                onDismissRequest = { openDialogPriority = false },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.aim_none), modifier = Modifier.clickable {
                            selectedPriority = 0
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                        Text(stringResource(R.string.aim_low), modifier = Modifier.clickable {
                            selectedPriority = 1
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.aim_medium), modifier = Modifier.clickable {
                            selectedPriority = 2
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.aim_high), modifier = Modifier.clickable {
                            selectedPriority = 3
                            openDialogPriority = false
                        }, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                },
                confirmButton = {}
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                stringResource(R.string.aim_auto_transfer),
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontSize = 15.sp
            )
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
    //val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
                Text(stringResource(R.string.date_picker_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}