package com.taibahai.quran;

import android.net.Uri;

import com.network.utils.AppClass;


public class StringUtils {
    //notifications constants
    public static final String NOTIFICATION_CHANNEL_ID = AppClass.Companion.getInstance().getPackageName();
    public static final String CHANNEL_NAME = "TaibahApp";
    public static final String CHANNEL_DESCRIPTION = "";


    public static final String NO_INDEX = "-1";
    public static final String PREV_ISLAM_BASIC_URL = "islamic_basic_url";
    public static final String PREV_ISLAM_BASIC_FILEPATH = "prev_islamic_basic_filepath";
    public static String[] iMonthNames = {"Muharram", "Safar", "Rabi-ul-Awwal",
            "Rabi-us-Sani", "Jamadi-ul-Awwal", "Jamadi-us-Sani", "Rajab",
            "Shaban", "Ramadan", "Shawwal", "Zil-Qadah", "Zul-Hijah"};

    public static final String FILENAME_FORMAT = "yyyy_MMM_dd_SSS";
    public static final String FILE_NAME = "_taibah.mp4";

    //client key
    public static final String CLIENT_KEY = "clientKey";
    public static final String CLIENT_KEY_HEADER = "SD78DF93_3947&MVNGHE1WONG";


    public static final String SURAH_NAME = "surah_name";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String ARRAY = "arraylist";
    public static final String BUNDLE = "bundle";
    public static final String ID = "id";
    public static final String CATEGORY_ID = "category_id";
    public static final String PARENT_CATEGORY = "parent_category";
    public static final String POSITION = "position";
    public static final String COMPLETED = "completed";

    public static final int SURAH_GROUP_ID = "QuranSurahList".hashCode();
    public static final String DUA_GROUP_ID = "DuaGroupId";
    public static final String ISLAMIC_BASICS = "islamic_basics";
    public static final String PREV_SURAH_URL = "prev_surah_url";
    public static final String PREV_SURAH_FILEPATH = "prev_surah_filepath";
    public static final String PREV_DUA_URL = "prev_dua_url";
    public static final String PREV_DUA_FILEPATH = "prev_dua_filepath";
    public static final String SURAH_FOLDER = "/surah/";
    public static final String DUA_FOLDER = "/dua/";
    public static final String ISLAMIC_BASIC_FOLDER = "/islamic_basics/";
    public static final String NO_STORAGE_SPACE = "NO_STORAGE_SPACE";
    public static final String FONT_SIZE = "font_size";
    public static final String SCROLL_SPEED = "scroll_speed";
    public static final String RESULT = "result";
    public static final String OBJECT = "object";
    public static final String US = "United States";
    public static final String NEW_YORK = "New York";
    public static final String MAKKI = "Meccan";
    public static final String MADNI = "Medinan";
    public static final String PARAM_TIMEZONE = "timezone";
    public static final String PARAM_AUDIO = "audio";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_DESC = "description";


    //apis
    public static final String DUAS_BY_CATEGORY = "duasbycategory";
    public static final String DUAS_BY_SUB_CATEGORY = "duasbysubcategory";
    public static final String METHOD_BY_TIMEZONE = "getPrayerMethodIdByTimeZone";
    public static final String DUAS = "getduas";

    public static final String ACTION_PLAY = "com.taibah.action_play";
    public static final String ACTION_PAUSE = "com.taibah.action_pause";
    public static final String ACTION_CLOSE = "com.taibah.action_close";

    public static final String DEFAULT_SCHOOL = "0";
    public static final String SHAFI = "0";
    public static final String HANAFI = "1";

    //defaults
//    public static final String DEFAULT_METHOD_NAME="Umm Al-Qura University, Makkah";
    public static final String DEFAULT_METHOD_NAME = "--";

//    public static final String ACTION_PLAY="com.taibah.action_play";
//    public static final String ACTION_PLAY="com.taibah.action_play";

    public static String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }


    //surah urls
    public static final String SURAH_URL_1 = "http://54.176.124.125/public/storage/attachments/ufTgeOKxW2af9klPvaXbR1h7tYMga0v4UtthuWoY.mp3";
    public static final String SURAH_URL_2 = "http://54.176.124.125/public/storage/attachments/wM5OZEwRT0bYYFXiBshU5f1Ug5l8LvcyjgfVWDhy.mp3";
}
