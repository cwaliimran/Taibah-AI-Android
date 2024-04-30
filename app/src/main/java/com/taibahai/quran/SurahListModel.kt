package com.taibahai.quran

import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.getAudioOutputDirectory
import com.tonyodev.fetch2.Download
import java.io.File
import java.io.Serializable

class SurahListModel(
    var id: String = "",
    var number: String = "",
    var name: String = "",
    var transliteration_en: String = "",
    var translation_en: String = "",
    var total_verses: String = "",
    var revelation_type: String = "",
    var audio: String = "",
    var isFav: Boolean = false,
    private var downloadId: Int = 0,
    var download: Download? = null
) : Serializable {

    fun getDownloadId(): Int {
        val url = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(audio)
        val yourFile = File(getAudioOutputDirectory(), url)
        return getUniqueId(AppClass.BASE_URL_1 + audio, yourFile.absolutePath)
    }

    private fun getUniqueId(s: String, absolutePath: String): Int {
        return s.hashCode() * 31 + absolutePath.hashCode()
    }

    fun setDownloadId(downloadId: Int) {
        this.downloadId = downloadId
    }

    override fun toString(): String {
        return "$number. $transliteration_en"
    }
}

