package com.example.habbitapp.view.ui.page

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.view.ui.customElement.CustomComponent
import com.example.habbitapp.view.ui.scaffold.AppDrawerScaffold
import com.example.habbitapp.viewmodel.TaskViewModel
import io.github.chouaibmo.rowkalendar.extensions.now
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LevelProductivityPage(
                           toAimsAndObjectivesPageClick: ()-> Unit,toSettingsPage: ()-> Unit,toMainPage: ()-> Unit){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var indicatorValue by remember { mutableIntStateOf(0) }
    val viewModel: TaskViewModel = viewModel()
    val date = LocalDate.now().toString()
    LaunchedEffect(date) {
        val allTasks  =  viewModel.getCountTask()
        indicatorValue = viewModel.getIndicator(date,allTasks)
    }
    AppDrawerScaffold(
        toAimsAndObjectivesPageClick = toAimsAndObjectivesPageClick,
        toSettingsPage = toSettingsPage,
        toProductivityPage = {   scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }},
        toMainPageClick = toMainPage,
        drawerState = drawerState
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch {drawerState.open()} }) {
                    Icon(Icons.Filled.Menu, stringResource(R.string.cd_menu))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(stringResource(R.string.title_habit), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        stringResource(R.string.title_app),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Green
                    )
                }
                

            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CustomComponent(indicatorValue = indicatorValue)
                    Text("Уровень продуктивности", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LevelProductivityPagePreview(){
    LevelProductivityPage(
        toAimsAndObjectivesPageClick = {},
        toSettingsPage = {},
        toMainPage = {}
    )
}
