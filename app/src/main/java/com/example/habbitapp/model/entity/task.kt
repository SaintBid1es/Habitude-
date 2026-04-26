package com.example.habbitapp.model.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habbitapp.model.database.Converters

@Entity(tableName = "task")
@TypeConverters(Converters::class)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val streak:Int = 0,
    val icon:String,
    val description: String,
    val backgroundColor: Int,
    val checkExec: Boolean = false,
    val repeat:Int,
    val days:List<Boolean>,
    val completionDates: List<String> = emptyList(),

){
    @get:Ignore
val actualColor: Color
    get() = Color(backgroundColor)


}

