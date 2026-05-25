package com.example.habbitapp.view.ui.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habbitapp.R
import kotlinx.coroutines.launch

@Composable
fun AppDrawerScaffold(drawerState: DrawerState, toMainPageClick: ()-> Unit, toAimsAndObjectivesPageClick: ()-> Unit, toSettingsPage: ()-> Unit, toProductivityPage: ()-> Unit, content: @Composable () -> Unit){

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(R.string.drawer_navigation), modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_habits)) },
                    icon = { Icon(painter = painterResource(R.drawable.habit_ic), contentDescription = null,modifier= Modifier.size(25.dp)) },
                    selected = false,
                    onClick = { toMainPageClick() }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_tasks)) },
                    icon = { Icon(painter = painterResource(R.drawable.mission_ic), contentDescription = null,modifier= Modifier.size(25.dp)) },
                    selected = false,
                    onClick = { toAimsAndObjectivesPageClick() }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_productivity)) },
                    icon = { Icon(painter = painterResource(R.drawable.graphic_ic), contentDescription = null,modifier= Modifier.size(25.dp)) },
                    selected = false,
                    onClick = { toProductivityPage() }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_settings)) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null,modifier= Modifier.size(25.dp)) },
                    selected = false,
                    onClick = { toSettingsPage() }
                )

            }
        }, content = content
    )
}

@Composable
@Preview
fun AppDrawerScaffoldPreview(){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
AppDrawerScaffold(drawerState,{},{},{},{},{})
}