package com.network.models

import android.os.Environment
import androidx.media3.exoplayer.offline.Download
import com.network.utils.AppClass
import com.network.utils.StringUtils
import java.io.File
import java.io.Serializable
import android.content.Context



data class ModelSurah(
    var id: String = "",
    var number: String = "",
    var name: String = "",
    var transliteration_en: String = "",
    var translation_en: String = "",
    var total_verses: String = "",
    var revelation_type: String = "",
    var audio: String = "",
    var type: String = "",
    var fav: Boolean = false,
    var download: Download? = null,
    var filePath: String? = null,
    var context: Context? = null


):Serializable {

    val downloadId: Int
        get() {
            val url: String = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(audio)
            val yourFile= File(AppClass.getAudioOutputDirectory(), url)
            filePath = yourFile.absolutePath
            return getUniqueId(AppClass.BASE_URL_1 + audio, yourFile.absolutePath)
        }

    private fun getUniqueId(s: String, absolutePath: String): Int {
        return s.hashCode() * 31 + absolutePath.hashCode()
    }

    fun getCurrentFile(context: Context): File? {
        return if (downloadId.toLong() != 0L) {
            val directory =
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            if (directory != null) {
                File(directory, StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(audio))
            } else {
                null
            }
        } else {
            null
        }
    }



}