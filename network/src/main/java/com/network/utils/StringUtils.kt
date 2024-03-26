package com.network.utils

import android.net.Uri
import com.network.utils.AppClass

 class StringUtils {

     companion object {
         val NOTIFICATION_CHANNEL_ID: String = AppClass.instance.packageName
         val CHANNEL_NAME = "Taibah AI"
         val CHANNEL_DESCRIPTION = ""


         val NO_INDEX = "-1"
         val PREV_ISLAM_BASIC_URL = "islamic_basic_url"
         val PREV_ISLAM_BASIC_FILEPATH = "prev_islamic_basic_filepath"
         var iMonthNames = arrayOf(
             "Muharram", "Safar", "Rabi-ul-Awwal",
             "Rabi-us-Sani", "Jamadi-ul-Awwal", "Jamadi-us-Sani", "Rajab",
             "Shaban", "Ramadan", "Shawwal", "Zil-Qadah", "Zul-Hijah"
         )

         val FILENAME_FORMAT = "yyyy_MMM_dd_SSS"
         val FILE_NAME = "_taibah.mp4"

         //client key
         val CLIENT_KEY = "clientKey"
         val CLIENT_KEY_HEADER = "SD78DF93_3947&MVNGHE1WONG"


         val SURAH_NAME = "surah_name"
         val STATUS = "status"
         val NAME = "name"
         val ARRAY = "arraylist"
         val BUNDLE = "bundle"
         val ID = "id"
         val CATEGORY_ID = "category_id"
         val PARENT_CATEGORY = "parent_category"
         val POSITION = "position"
         val COMPLETED = "completed"

         val SURAH_GROUP_ID = "QuranSurahList".hashCode()
         val DUA_GROUP_ID = "DuaGroupId"
         val ISLAMIC_BASICS = "islamic_basics"
         val PREV_SURAH_URL = "prev_surah_url"
         val PREV_SURAH_FILEPATH = "prev_surah_filepath"
         val PREV_DUA_URL = "prev_dua_url"
         val PREV_DUA_FILEPATH = "prev_dua_filepath"
         val SURAH_FOLDER = "/surah/"
         val DUA_FOLDER = "/dua/"
         val ISLAMIC_BASIC_FOLDER = "/islamic_basics/"
         val NO_STORAGE_SPACE = "NO_STORAGE_SPACE"
         val FONT_SIZE = "font_size"
         val SCROLL_SPEED = "scroll_speed"
         val RESULT = "result"
         val OBJECT = "object"
         val US = "United States"
         val NEW_YORK = "New York"
         val MAKKI = "Meccan"
         val MADNI = "Medinan"
         val PARAM_TIMEZONE = "timezone"
         val PARAM_AUDIO = "audio"
         val PARAM_TITLE = "title"
         val PARAM_DESC = "description"


         //apis
         val DUAS_BY_CATEGORY = "duasbycategory"
         val DUAS_BY_SUB_CATEGORY = "duasbysubcategory"
         val METHOD_BY_TIMEZONE = "getPrayerMethodIdByTimeZone"
         val DUAS = "getduas"

         val ACTION_PLAY = "com.taibah.action_play"
         val ACTION_PAUSE = "com.taibah.action_pause"
         val ACTION_CLOSE = "com.taibah.action_close"

         val DEFAULT_SCHOOL = "0"
         val SHAFI = "0"
         val HANAFI = "1"

         //defaults
         //    public static final String DEFAULT_METHOD_NAME="Umm Al-Qura University, Makkah";
         val DEFAULT_METHOD_NAME = "--"


//    public static final String ACTION_PLAY="com.taibah.action_play";
//    public static final String ACTION_PLAY="com.taibah.action_play";

         //    public static final String ACTION_PLAY="com.taibah.action_play";
         //    public static final String ACTION_PLAY="com.taibah.action_play";
         fun getNameFromUrl(url: String?): String? {
             return Uri.parse(url).lastPathSegment
         }

     }

}