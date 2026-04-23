package com.example.habbitapp.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habbitapp.Converters

@Entity(tableName = "aims")
@TypeConverters(Converters::class)
data class Aims(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val icon:String,
    val description: String,
    val checkExec: Boolean = false,
    val category :Int
    )