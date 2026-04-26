package com.example.habbitapp.view.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsPage(toMainPageClick:()-> Unit){
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)){
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment =  Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Settings", modifier = Modifier.clickable{
                toMainPageClick()
            }

            )
        }
    }
}


@Composable
@Preview
fun SettingsPagePreview(){
    SettingsPage({})
}