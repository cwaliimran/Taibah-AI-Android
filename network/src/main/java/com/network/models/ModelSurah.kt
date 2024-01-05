package com.network.models

import androidx.media3.exoplayer.offline.Download
import com.network.utils.AppClass
import com.network.utils.StringUtils
import java.io.File
import java.io.Serializable


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
    var download: Download? = null


):Serializable {

    val downloadId: Int
        get() {
            val url: String = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(audio)
            val yourFile= File(AppClass.getAudioOutputDirectory(), url)
            return getUniqueId(AppClass.BASE_URL_1 + audio, yourFile.absolutePath)
        }

    private fun getUniqueId(s: String, absolutePath: String): Int {
        return s.hashCode() * 31 + absolutePath.hashCode()
    }
}