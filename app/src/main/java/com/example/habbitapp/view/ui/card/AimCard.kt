package com.example.habbitapp.view.ui.card

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.viewmodel.AimViewModel
import java.time.LocalDate


@SuppressLint("NewApi")
@Composable
fun AimCard(aims: Aims, onUpdatePage: () -> Unit) {
    val context = LocalContext.current
//    val mediaPlayerSuccess = remember {
//        MediaPlayer.create(context, R.raw.notification)
//    }
//    val mediaPlayerUnSuccess = remember {
//        MediaPlayer.create(context, R.raw.otmena)
//    }
    val viewmodel: AimViewModel = viewModel()

    var onEnabledList by remember { mutableStateOf(false) }


    val color by animateColorAsState(
        targetValue = if (aims.checkExec){ Color.Green}
        else{
            Color.Gray
        }, animationSpec = tween(durationMillis = 700)
    )


    val size by animateSizeAsState(
        targetValue = if (aims.checkExec) Size(24f,24f) else Size(27f,27f)
        , animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val sizeSubAims by animateSizeAsState(
        targetValue = if (aims.checkExec) Size(15f,15f) else Size(17f,17f)
        , animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )


    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                onUpdatePage()
            },
        shape = RoundedCornerShape(5.dp),

    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.padding(5.dp)) {
                    Text(aims.name, textDecoration = if (aims.checkExec) {TextDecoration.LineThrough }else {
                        null
                    })
                    Row() {
                        Text(
                            "${aims.subAims.filter { it.value == true }.size}/${aims.subAims.size} ",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable {
                                onEnabledList = !onEnabledList
                            })
                        Text(text = if (onEnabledList) "⌵" else ">",
                            fontSize = 12.sp,
                            color = Color.Gray,modifier = Modifier.clickable {
                                onEnabledList = !onEnabledList
                            })
                        Text(" ${aims.category}")


                    }

                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = {
                        if (!aims.checkExec) {
//                        mediaPlayerSuccess.start()
                            val newAim = aims.copy(checkExec = true)
                            viewmodel.updateAim(newAim)
                        } else {
//                        mediaPlayerUnSuccess.start()
                            val newAim = aims.copy(checkExec = false)
                            viewmodel.updateAim(newAim)
                        }
                    }) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = if (aims.checkExec) "Отменить" else "Выполнить",
                            tint = color,
                            modifier = Modifier.size(size.width.dp, size.height.dp)
                        )
                    }
                }

            }

        }
        if (onEnabledList){
            LazyColumn(modifier = Modifier.padding(5.dp)) {
                items(aims.subAims.toList()){it->
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(it.first, textDecoration = if (it.second) {TextDecoration.LineThrough }else {
                            null
                        }
                        )
                        IconButton(onClick = {
                            val newAim = aims.copy(subAims =mapOf(it.first to !it.second))
                            viewmodel.updateAim(newAim)
                        }) {
                            val colorSubAim by animateColorAsState(
                                targetValue = if (it.second) { Color.Green}
                                else{
                                    Color.Gray
                                }, animationSpec = tween(durationMillis = 700)
                            )
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = colorSubAim,
                                modifier = Modifier.size(sizeSubAims.width.dp, sizeSubAims.height.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}

@SuppressLint("NewApi")
@Composable
@Preview
fun AimCardPreview(){
    val map:Map<String, Boolean> =mapOf("Купить продукты" to true,"Порезать овощи" to false,"В кастрюлю все закинть" to false)
    val date = LocalDate.now().toString()
    val aim = Aims(0,"Приготовить ужин",false,"Дом",1,false,map,date)

    AimCard(aim,{})
}



