package com.example.habbitapp.view.ui.widgets

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import java.time.LocalDate

import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.Row as GlanceRow
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text

import com.example.habbitapp.model.database.ItemDatabase
import com.example.habbitapp.model.entity.Task
import com.example.habbitapp.model.utils.StreakManager
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class MyAppWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = ItemDatabase.getInstance(context).taskDao()
        val dbTasks = withContext(Dispatchers.IO) {
            dao.getAllTasks()
        }

        provideContent {
            // Получаем текущие Prefs виджета прямо внутри provideContent
            val prefs = currentState<Preferences>()

            // Маппим задачи из БД, подменяя их стейт на актуальный из DataStore для мгновенного отклика
            val tasks = dbTasks.map { task ->
                val localCheckKey = booleanPreferencesKey("task_check_${task.id}")
                val localStreakKey = intPreferencesKey("task_streak_${task.id}")

                task.copy(
                    checkExec = prefs[localCheckKey] ?: task.checkExec,
                    streak = prefs[localStreakKey] ?: task.streak
                )
            }

            MyContent(tasks = tasks)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MyContent(tasks: List<Task>) {
    GlanceColumn(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.White),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Сегодня:", modifier = GlanceModifier.padding(12.dp))

        val today = LocalDate.now().dayOfWeek.value - 1
        val todayTasks = tasks.filter { it.days.getOrNull(today) == true }

        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
            todayTasks.forEach { task ->
                item(itemId = task.id.toLong()) {
                    TaskGlanceCard(task = task)
                }
            }
        }
    }
}

@Composable
private fun TaskGlanceCard(task: Task) {
    GlanceRow(
        modifier = GlanceModifier
            .padding(5.dp)
            .fillMaxWidth()
            .background(Color(task.backgroundColor))
            .cornerRadius(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = task.icon,
            modifier = GlanceModifier.padding(10.dp)
        )
        GlanceColumn(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.name,
                style = androidx.glance.text.TextStyle(fontSize = 14.sp)
            )
            GlanceRow(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\uD83D\uDD25 ${task.streak} дней",
                    style = androidx.glance.text.TextStyle(fontSize = 10.sp)
                )
            }
        }

        GlanceRow(
            modifier = GlanceModifier.padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.End
        ) {
            Button(
                text = if (task.checkExec) "✓" else "◦",
                onClick = actionRunCallback<UpdateTaskAction>(
                    actionParametersOf(UpdateTaskAction.TaskIdKey to task.id.toLong())
                )
            )
        }
    }
}

class UpdateTaskAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskIdKey] ?: return
        val dao = ItemDatabase.getInstance(context).taskDao()

        // Переменные для записи в DataStore
        var newCheckExec = false
        var newStreak = 0

        withContext(Dispatchers.IO) {
            val task = dao.getTaskById(taskId.toInt())
            if (task != null) {
                val updatedTask = if (!task.checkExec) {
                    StreakManager.onTaskCompleted(task)
                } else {
                    StreakManager.onTaskUncompleted(task)
                }

                // Сохраняем локально новые значения для UI
                newCheckExec = updatedTask.checkExec
                newStreak = updatedTask.streak

                // Асинхронно пишем в Room
                dao.update(updatedTask)
            }
        }

        // МГНОВЕННОЕ ОБНОВЛЕНИЕ: Сначала пишем новое состояние в быструю память DataStore виджета.
        // Как только updateAppWidgetState завершится, Glance моментально перерисует экран.
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                val localCheckKey = booleanPreferencesKey("task_check_${taskId}")
                val localStreakKey = intPreferencesKey("task_streak_${taskId}")

                this[localCheckKey] = newCheckExec
                this[localStreakKey] = newStreak
            }
        }

        // Уведомляем систему об изменениях инстанса
        MyAppWidget().update(context, glanceId)
    }

    companion object {
        val TaskIdKey = ActionParameters.Key<Long>("task_id_key")
    }
}
