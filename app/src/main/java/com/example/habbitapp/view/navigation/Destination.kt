package com.example.habbitapp.view.navigation

sealed class Destination(val route: String) {

    data object First : Destination(ROUTE_FIRST)
    data object AimsAndObjectives : Destination(ROUTE_AIMSANDOBJECTIVES)

    data object Second : Destination(ROUTE_SECOND)
    data object AddTask : Destination(ADD_TASK)
    data object AddAim : Destination(ADD_AIM)
    data object EditTask : Destination("route_editTask/{taskId}") {
        fun passId(id: Int): String = "route_editTask/$id"
    }
    data object EditAim : Destination("route_editAim/{aimId}") {
        fun passId(id: Int): String = "route_editAim/$id"
    }

    companion object {
        private const val ROUTE_FIRST = "route_first"
        private const val ADD_AIM = "route_addAim"
        private const val ROUTE_SECOND = "route_second"
        private const val ADD_TASK = "route_addTask"
        private const val EDIT_TASK = "route_editTask"
        private const val ROUTE_AIMSANDOBJECTIVES = "route_aimsAndObjectives"
    }
}