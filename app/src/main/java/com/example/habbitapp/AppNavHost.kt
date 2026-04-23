package com.example.habbitapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habbitapp.ui.page.AddTaskPage
import com.example.habbitapp.ui.page.AimsAndObjectibesPage
import com.example.habbitapp.ui.page.SettingsPage
import com.example.habbitapp.ui.page.UpdateAimPage
import com.example.habbitapp.ui.page.UpdateTaskPage

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.First.route,
    ) {

        composable(route = Destination.First.route) {
            MainPage(
                toAddTaskPageClick = {
                    navController.navigate(Destination.AddTask.route)
                },
                onTaskClick = { taskId->
                    navController.navigate(Destination.EditTask.passId(taskId))
                }, toAimsAndObjectivesPageClick = {
                    navController.navigate(Destination.AimsAndObjectives.route)
                }
            ) 
        }

        composable(route = Destination.Second.route) {
            SettingsPage(toMainPageClick = {
                navController.popBackStack()
            })
        }
        composable(route = Destination.AimsAndObjectives.route) {
            AimsAndObjectibesPage(
                toMainPageClick = {
                    navController.navigate(Destination.First.route)
                },
                toAddAimsPageClick = {
                    navController.navigate(Destination.AddAim.route)
                },
                onAimsClick = {
                        aimId->
                    navController.navigate(Destination.EditAim.passId(aimId))
                }
            )
        }
        composable(route = Destination.AddTask.route) {
            AddTaskPage(
                toMainPageClick = {
                    navController.navigate(Destination.First.route)
                }
            )
        }
        composable(
            route = Destination.EditTask.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0

            UpdateTaskPage(
                idTask = taskId,
                toMainPageClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destination.EditAim.route,
            arguments = listOf(
                navArgument("aimId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val aimId = backStackEntry.arguments?.getInt("aimId") ?: 0

            UpdateAimPage(
                idAim = aimId,
                toAimsPageClick = {
                    navController.popBackStack()
                }
            )
        }


    }
}