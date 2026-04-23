package com.example.habbitapp.ui.page

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
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
import com.example.habbitapp.entity.Aims
import com.example.habbitapp.entity.Reminder
import com.example.habbitapp.entity.Task
import com.example.habbitapp.setTimeNotification
import com.example.habbitapp.ui.card.ColorCircleRow
import com.example.habbitapp.viewmodel.AimViewModel
import com.example.habbitapp.viewmodel.ReminderViewModel
import com.example.habbitapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAimPage(toAimsPageClick: ()-> Unit) {

    var text by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableIntStateOf(1) }
    val viewModelAim : AimViewModel = viewModel()
    var openDialogIcon by remember {mutableStateOf(false)}
    var selectedIcon by remember { mutableStateOf("📚") }
    val scope = rememberCoroutineScope()
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
            IconButton(onClick = toAimsPageClick) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }
            Text("Save", modifier = Modifier.clickable{
                scope.launch {

                    val aim = Aims(
                        0,
                        text,
                        selectedIcon,
                        description,
                        false,
                        selectedCategory
                    )
                    viewModelAim.insertAim(aim)

                    toAimsPageClick()
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
                modifier = Modifier.width(200.dp), singleLine = true, maxLines = 1
            )

        }
        Spacer(modifier = Modifier.padding(top = 10.dp))

        Text("Category", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.spacedBy(60.dp)) {
            FilterChip("Daily",selectedCategory==1,{selectedCategory=1})
            FilterChip("Weekly",selectedCategory==2,{selectedCategory=2})
            FilterChip("Monthly",selectedCategory==3,{selectedCategory=3})
        }





    }
}


@Preview
@Composable
fun AddAimPagePreviw(){
    AddAimPage({})
}