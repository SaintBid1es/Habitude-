    package com.example.habbitapp.model.utils
    
    import android.content.Context
    import android.util.Log
    import com.example.habbitapp.model.database.ItemDatabase
    import com.example.habbitapp.model.entity.StatFailHabit
    import java.time.YearMonth
    
    object StatFailHabitManager {
    
        private const val TAG = "StatFailHabitManager"
    
        suspend fun checkAndReset(context: Context) {
    
            try {
    
                val database = ItemDatabase.getInstance(context)
    
                val dao = database.statFailHabitDao()
    
                val currentMonth = YearMonth.now().toString()
    
                val stat = dao.getStatFailHabit()
    
    
                if (stat == null) {
    
                    dao.insert(
                        StatFailHabit(
                            id = 1,
                            notPower = 0,
                            notTime = 0,
                            forgot = 0,
                            other = 0,
                            monthKey = currentMonth
                        )
                    )
    
                    Log.d(
                        TAG,
                        "StatFailHabit created for $currentMonth"
                    )
    
                    return
                }
    
                /*
                 * Если месяц изменился —
                 * обнуляем статистику.
                 */
                if (stat.monthKey != currentMonth) {
    
                    dao.resetStats(currentMonth)
    
                    Log.d(
                        TAG,
                        "StatFailHabit reset: " +
                                "${stat.monthKey} -> $currentMonth"
                    )
                }
    
            } catch (e: Exception) {
    
                Log.e(
                    TAG,
                    "Error checking StatFailHabit",
                    e
                )
            }
        }
    }