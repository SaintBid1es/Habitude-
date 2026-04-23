package com.example.habbitapp

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromListIntToString(days: List<Int>): String {
        return days.joinToString(",")
    }
    @TypeConverter
    fun fromBooleanList(list: List<Boolean>): String {
        return list.joinToString(separator = ",") { if (it) "1" else "0" }
    }
    @TypeConverter
    fun toBooleanList(data: String): List<Boolean> {
        if (data.isEmpty()) return emptyList()
        return data.split(",").map { it == "1" }
    }
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(separator = ",")
    }


    @TypeConverter
    fun toStringList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }

    @TypeConverter
    fun fromStringToListInt(daysString: String): List<Int> {
        return if (daysString.isEmpty()) {
            emptyList()
        } else {
            daysString.split(",").map { it.toInt() }
        }
    }
}