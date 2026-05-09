package com.example.habbitapp.view.ui.page

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.model.utils.SettingsManager

import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.util.Locale

@SuppressLint("SuspiciousIndentation")
@Composable
fun SettingsPage(
                 toAimsAndObjectivesPageClick: ()-> Unit,toProductivityPage: ()-> Unit,toMainPageClick: ()-> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val checkedStateLanguage = rememberSaveable { mutableStateOf(AppCompatDelegate.getApplicationLocales().toLanguageTags() == "ru") }

    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context.applicationContext) }
    val isDarkMode by settingsManager.isDarkMode.collectAsState(initial = false)
        ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(R.string.drawer_navigation), modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_habits)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.habit_ic),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = {
                        toMainPageClick()
                    }

                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_tasks)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.mission_ic),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = { toAimsAndObjectivesPageClick() }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_productivity)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.graphic_ic),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = {
                        toProductivityPage()
                    }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_settings)) },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )

            }
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)) {

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
                        text = (stringResource(R.string.settings)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                }


            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.SpaceBetween, modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)) {
                Text(text = ("RU/EN"), fontWeight = FontWeight.Bold)
                Switch(
                    checked = checkedStateLanguage.value,
                    onCheckedChange = {   isChecked ->
                        checkedStateLanguage.value = isChecked
                        val lang = if (isChecked) "ru" else "en"
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.SpaceBetween, modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)) {
                Text(text = ("Theme(Light/Dark)"), fontWeight = FontWeight.Bold)
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { checked ->
                        scope.launch {
                            settingsManager.saveDarkMode(checked)
                            AppCompatDelegate.setDefaultNightMode(
                                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                                else AppCompatDelegate.MODE_NIGHT_NO
                            )
                        }
                    }
                )
            }
        }

    }
}


@Composable
@Preview(locale = "en")
fun SettingsPagePreview(){
    SettingsPage(
        toAimsAndObjectivesPageClick = {},
        toProductivityPage = {},
        toMainPageClick = {}
    )
}