package com.example.habbitapp.ui.card

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntSize
import androidx.core.graphics.toColor
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.R
import com.example.habbitapp.entity.Task
import com.example.habbitapp.utils.StreakManager
import com.example.habbitapp.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.sign


@SuppressLint("NewApi")
@Composable
fun TaskCard(task: Task, onUpdatePage: () -> Unit) {
    val context = LocalContext.current
//    val mediaPlayerSuccess = remember {
//        MediaPlayer.create(context, R.raw.notification)
//    }
//    val mediaPlayerUnSuccess = remember {
//        MediaPlayer.create(context, R.raw.otmena)
//    }
    val viewmodel: TaskViewModel = viewModel()

    val color by animateColorAsState(
        targetValue = if (task.checkExec){ Color.Green}
        else{
            Color.Gray
        }, animationSpec = tween(durationMillis = 700)
    )
    val size by animateSizeAsState(
        targetValue = if (task.checkExec) Size(24f,24f) else Size(27f,27f)
        , animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(task.id) {
        val resetTask = StreakManager.resetCheckExecForNewDay(task)
        if (resetTask != task) {
            viewmodel.updateTask(resetTask)
        }
    }
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                onUpdatePage()
            },
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(task.backgroundColor)
        ),
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(task.icon, modifier = Modifier.padding(10.dp))
            Column {

                    Text(task.name)
                Row {
                    Text("\uD83D\uDD25", fontSize = 10.sp)
                    AnimatedContent(
                        targetState = task.streak,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            } else {
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            }.using(
                                SizeTransform(clip = false)
                            )
                        }
                    ) { targetCount ->
                        Text(text = "$targetCount", fontSize = 10.sp)
                    }
                    Text("Days", fontSize = 10.sp)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    val updatedTask = if (!task.checkExec) {
//                        mediaPlayerSuccess.start()
                        StreakManager.onTaskCompleted(task)

                    } else {
//                        mediaPlayerUnSuccess.start()
                        StreakManager.onTaskUncompleted(task)
                    }
                    viewmodel.updateTask(updatedTask)
                }) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = if (task.checkExec) "Отменить" else "Выполнить",
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
fun TaskPreview() {
    val task = Task(
        0,
        "text",
        0,
        "\uD83D\uDCD6",
        "description",
        Color.White.toArgb(),
        true,
        0,
        mutableListOf<Boolean>(false)
    )

    TaskCard(task, {})
}
