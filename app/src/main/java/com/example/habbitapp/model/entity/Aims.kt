package com.example.habbitapp.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habbitapp.model.database.Converters
import java.time.LocalDate

@Entity(tableName = "aims")
@TypeConverters(Converters::class)
data class Aims(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val checkExec: Boolean = false,
    val category : String,
    val priority:Int,
    val autotransfer:Boolean,
    val subAims: Map<String,Boolean>,
    val date:String
    )
