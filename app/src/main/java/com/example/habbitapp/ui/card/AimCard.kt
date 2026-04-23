package com.example.habbitapp.ui.card

import android.annotation.SuppressLint
import android.media.MediaPlayer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.entity.Aims
import com.example.habbitapp.entity.Task
import com.example.habbitapp.utils.StreakManager
import com.example.habbitapp.viewmodel.AimViewModel
import com.example.habbitapp.viewmodel.TaskViewModel


@SuppressLint("NewApi")
@Composable
fun AimCard(aims: Aims, onUpdatePage: () -> Unit) {
    val context = LocalContext.current
    val mediaPlayerSuccess = remember {
        MediaPlayer.create(context, R.raw.notification)
    }
    val mediaPlayerUnSuccess = remember {
        MediaPlayer.create(context, R.raw.otmena)
    }
    val viewmodel: AimViewModel = viewModel()



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


    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                onUpdatePage()
            },
        shape = RoundedCornerShape(5.dp),

    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(aims.icon, modifier = Modifier.padding(10.dp))
            Column {
                Text(aims.name)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                     if (!aims.checkExec) {
                        mediaPlayerSuccess.start()
                   val newAim =     aims.copy(checkExec = true)
                         viewmodel.updateAim(newAim)
                     } else {
                        mediaPlayerUnSuccess.start()
                         val newAim = aims.copy(checkExec = false)
                         viewmodel.updateAim(newAim)
                    }
                }) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = if (aims.checkExec) "Отменить" else "Выполнить",
                        tint = color,
                        modifier = Modifier.size(size.width.dp,size.height.dp)
                    )
                }
            }

        }

    }

}

@Composable
@Preview
fun AimCardPreview(){
    val aim = Aims(0,"","","",false,1)
    AimCard(aim,{})
}



