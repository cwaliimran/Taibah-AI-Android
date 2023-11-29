package com.network.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT = "yyyy/MM/dd : HH:mm:ss"
fun Long.convertLongToDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return format.format(date)
}

//2022-11-11 13:59:00
const val INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val OUT_PUT_DATE_FORMAT = "dd-MM-yyyy"
const val OUT_PUT_TIME_FORMAT = "hh:mm a"

//format date
fun String.formatDate(inputFormat: String, outputFormat: String): String {
    return try {
        val date = SimpleDateFormat(inputFormat, Locale.getDefault()).parse(this)    // parse input
        SimpleDateFormat(outputFormat, Locale.getDefault()).format(date as Date)    // format output
    } catch (e: ParseException) {
        e.toString()
    }
}

//today date

fun String.todayDate(): String {
    val sdf = SimpleDateFormat(this, Locale.getDefault())
    return try {
        sdf.format(Date())
    } catch (e: Exception) {
        "Format not valid"
    }

}



//utcDate: String
fun String.convertUtcToLocal(): String {
    val utcDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val utcDateTime = utcDateFormat.parse(this)

    val localDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    localDateFormat.timeZone = TimeZone.getDefault()
    val localDateTime = utcDateTime?.let { Date(it.time) }

    return localDateTime?.let { localDateFormat.format(it) } ?: ""
}