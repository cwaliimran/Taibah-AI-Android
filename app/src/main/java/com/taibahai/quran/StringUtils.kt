package com.taibahai.quran

import android.net.Uri
import com.network.utils.AppClass.Companion.instance

object StringUtils {
    //notifications constants
    @JvmField
    val NOTIFICATION_CHANNEL_ID = instance.packageName
    const val CHANNEL_NAME = "TaibahAI"
    const val CHANNEL_DESCRIPTION = ""
    const val NO_INDEX = "-1"
    const val SURAH_NAME = "surah_name"
    const val ARRAY = "arraylist"
    const val BUNDLE = "bundle"
    val SURAH_GROUP_ID = "QuranSurahList".hashCode()
    const val PREV_SURAH_URL = "prev_surah_url"
    const val PREV_SURAH_FILEPATH = "prev_surah_filepath"
    const val SURAH_FOLDER = "/surah/"
    const val FONT_SIZE = "font_size"
    const val SCROLL_SPEED = "scroll_speed"
    const val OBJECT = "object"
    const val MAKKI = "Meccan"
    const val MADNI = "Medinan"
    const val ACTION_PLAY = "com.taibah.action_play"
    const val ACTION_PAUSE = "com.taibah.action_pause"
    const val ACTION_CLOSE = "com.taibah.action_close"

    @JvmStatic
    fun getNameFromUrl(url: String?): String? {
        return Uri.parse(url).lastPathSegment
    }

}