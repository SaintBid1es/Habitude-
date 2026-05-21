package com.example.habbitapp.model.database

import androidx.compose.runtime.toMutableStateMap
import androidx.room.TypeConverter
import java.util.Collections.emptyMap

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
    @TypeConverter
    fun fromMapStringBoolean(map: MutableMap <String, Boolean>): String {
        if (map.isEmpty()) return ""
        return map.entries.joinToString(separator = ";") { entry ->
            "${entry.key}:${if (entry.value) "1" else "0"}"
        }
    }

    @TypeConverter
    fun fromMapStringBoolean(map: HashMap<String, Boolean>?): String {
        if (map.isNullOrEmpty()) return ""
        return map.entries.joinToString(separator = ";") { (k, v) ->
            "$k:${if (v) "1" else "0"}"
        }
    }

    @TypeConverter
    fun toMapStringBoolean(data: String): HashMap<String, Boolean>? {
        if (data.isEmpty()) return null
        val result = HashMap<String, Boolean>()
        data.split(";").forEach { pair ->
            val parts = pair.split(":")
            if (parts.size == 2) {
                result[parts[0]] = parts[1] == "1"
            }
        }
        return result
    }
}