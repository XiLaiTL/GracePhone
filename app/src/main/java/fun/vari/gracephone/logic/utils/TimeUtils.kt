package `fun`.vari.gracephone.logic.utils

import `fun`.vari.gracephone.R
import androidx.annotation.StringRes
import kotlinx.datetime.*
import java.time.DayOfWeek

fun getDateTime():String{
    val currentMoment: Instant = Clock.System.now()
    //val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
    val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    return datetimeInSystemZone.toString()
}
fun getDate():String{
    val dateInSystemZone: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return dateInSystemZone.toString()
}
fun getDateZone():LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
fun getDateForCalendar():String{
    val dateInSystemZone: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${dateInSystemZone.year}-${dateInSystemZone.month.value}-${dateInSystemZone.dayOfMonth}"
}
@StringRes
fun getWeek():Int{
    val dateInSystemZone: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return when(dateInSystemZone.dayOfWeek){
        DayOfWeek.MONDAY -> R.string.day1
        DayOfWeek.TUESDAY -> R.string.day2
        DayOfWeek.WEDNESDAY -> R.string.day3
        DayOfWeek.THURSDAY -> R.string.day4
        DayOfWeek.FRIDAY -> R.string.day5
        DayOfWeek.SATURDAY -> R.string.day6
        DayOfWeek.SUNDAY -> R.string.day7
    }
}
fun String.toDateTime()=Instant.parse(this)