package com.taibahai.quran
import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.taibahai.R
import com.tonyodev.fetch2.Download
import java.io.File
import java.io.Serializable

class SurahListModel : Serializable {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("number")
    @Expose
    var number: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("transliteration_en")
    @Expose
    var transliterationEn: String? = null

    @SerializedName("translation_en")
    @Expose
    var translationEn: String? = null

    @SerializedName("total_verses")
    @Expose
    var totalVerses: String? = null

    @SerializedName("revelation_type")
    @Expose
    var revelationType: String? = null

    @SerializedName("audio")
    @Expose
    var audio: String? = null

    var isFav = false
    private var downloadId = 0
    var download: Download? = null

    fun getDownloadId(context: Context): Int {
        val BASE_URL_1 = "https://taibahislamic.com/admin/"
        val url = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(audio)
        val yourFile = File(getAudioOutputDirectory(context), url)
        return getUniqueId(BASE_URL_1 + audio, yourFile.absolutePath)
    }

    private fun getUniqueId(s: String, absolutePath: String): Int {
        return s.hashCode() * 31 + absolutePath.hashCode()
    }

    fun setDownloadId(downloadId: Int) {
        this.downloadId = downloadId
    }

    companion object {
        fun getAudioOutputDirectory(context: Context): File {
            val mediaStorageDir = File(
                context.filesDir.toString() + "/" +
                        context.getString(R.string.app_name) + "/Audios"
            )
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            return mediaStorageDir
        }
    }
}
