package com.example.habbitapp.view.ui.card

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habbitapp.model.entity.Aims
import com.example.habbitapp.viewmodel.AimViewModel
import com.example.habbitapp.R
import java.time.LocalDate


@SuppressLint("NewApi")
@Composable
fun AimCard(aims: Aims, onUpdatePage: () -> Unit) {
    val context = LocalContext.current
    val viewmodel: AimViewModel = viewModel()

    var onEnabledList by remember { mutableStateOf(false) }

    var lineThrough: Boolean = aims.checkExec

    val color by animateColorAsState(
        targetValue = if (aims.checkExec) Color.Green else Color.Gray,
        animationSpec = tween(durationMillis = 700)
    )

    val size by animateSizeAsState(
        targetValue = if (aims.checkExec) Size(24f, 24f) else Size(27f, 27f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val sizeSubAims by animateSizeAsState(
        targetValue = if (aims.checkExec) Size(15f, 15f) else Size(17f, 17f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Проверка на выполнение всех подзадач
    if (aims.subAims?.filter { it.value == true }?.size == aims.subAims?.size) {
        if (!aims.subAims.isNullOrEmpty()) {
            lineThrough = true
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
    ) {
        Column {
            // Основной ряд с контентом и кнопкой
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Левая часть с текстом (занимает всё свободное место, но с ограничением)
                Column(
                    modifier = Modifier
                        .weight(1f)  // ← КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: занимает оставшееся место
                        .padding(5.dp)
                ) {
                    // Название задачи с ограничением по строкам
                    Text(
                        text = aims.name,
                        textDecoration = if (lineThrough) TextDecoration.LineThrough else null,
                        maxLines = 2,  // ← Ограничиваем максимум 2 строками
                        overflow = TextOverflow.Ellipsis,  // ← Добавляем многоточие
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Строка с информацией о подзадачах и категории
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (aims.subAims != null) {
                            Text(
                                text = "${aims.subAims.filter { it.value == true }.size}/${aims.subAims.size}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.clickable { onEnabledList = !onEnabledList }
                            )
                            Text(
                                text = if (onEnabledList) "⌵" else ">",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.clickable { onEnabledList = !onEnabledList }
                            )
                            Text(
                                text = aims.category ?: "",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Правая часть - кнопка выполнения (фиксированный размер)
                Box(
                    modifier = Modifier
                        .requiredSize(48.dp),  // ← Фиксированный размер контейнера
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (!aims.checkExec) {
                                val newAim = aims.copy(checkExec = true)
                                viewmodel.updateAim(newAim)
                            } else {
                                val newAim = aims.copy(checkExec = false)
                                viewmodel.updateAim(newAim)
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = if (aims.checkExec)
                                stringResource(R.string.cd_undo)
                            else
                                stringResource(R.string.cd_complete),
                            tint = color,
                            modifier = Modifier.size(size.width.dp, size.height.dp)
                        )
                    }
                }
            }

            // Список подзадач (раскрывающийся)
            if (onEnabledList && aims.subAims != null) {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                        .heightIn(max = 300.dp)
                ) {
                    items(aims.subAims.toList()) { (subAimTitle, isCompleted) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Текст подзадачи с ограничением
                            Text(
                                text = subAimTitle,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Кнопка выполнения подзадачи
                            IconButton(
                                onClick = {
                                    val list = aims.subAims?.toMutableMap() ?: mutableMapOf()
                                    list[subAimTitle] = !isCompleted
                                    val newAim = aims.copy(subAims = list)
                                    viewmodel.updateAim(newAim)
                                }
                            ) {
                                val colorSubAim by animateColorAsState(
                                    targetValue = if (isCompleted) Color.Green else Color.Gray,
                                    animationSpec = tween(durationMillis = 700)
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
}

@SuppressLint("NewApi")
@Composable
@Preview
fun AimCardPreview() {
    val map: MutableMap<String, Boolean> = mutableMapOf(
        "Купить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужинаКупить продукты для ужина" to true,
        "Порезать овощи" to false,
        "В кастрюлю все закинуть" to false
    )
    val date = LocalDate.now().toString()
    val aim = Aims(0, "Приготовить ужин", false, "Дом", 1, false, map, date)

    MaterialTheme {
        AimCard(aim, {})
    }
}

// Дополнительный превью для тестирования длинных текстов
@SuppressLint("NewApi")
@Composable
@Preview(name = "Long text preview")
fun AimCardLongTextPreview() {
    val map: MutableMap<String, Boolean> = mutableMapOf(
        "Очень длинная подзадача которая никак не помещается в одну строку и должна переноситься" to false
    )
    val date = LocalDate.now().toString()
    val aim = Aims(
        0,
        "Очень очень очень длинное название задачи которое никак не помещается в одну строку и должно быть обрезано с многоточием",
        false,
        "Очень длинная категория",
        1,
        false,
        map,
        date
    )

    MaterialTheme {
        AimCard(aim, {})
    }
}