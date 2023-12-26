package com.taibahai.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.URLUtil
import androidx.core.net.toUri
import com.network.utils.AppClass
import java.io.File

class FileDownloader(private val context: Context) {

    private val TAG = "FileDownloader"

    fun downloadFile(url: String, title: String, description: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(title)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManager.enqueue(request)
    }

    fun resumeDownload(downloadId: Long) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.remove(downloadId)
    }

    @SuppressLint("Range")
    fun isDownloadInProgress(downloadId: Long): Boolean {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        return cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_RUNNING
    }

    fun saveDownloadRequest(url: String, title: String, description: String) {


        AppClass.sharedPref.storeString("PREV_SURAH_URL",url)
        AppClass.sharedPref.storeString("PREV_SURAH_FILEPATH","${Environment.DIRECTORY_DOWNLOADS}/$title")
    }

    fun getSavedDownloadRequest(): Triple<String, String, String>? {
        val url = AppClass.sharedPref.getString("PREV_SURAH_URL", "") ?: ""
        val filePath = AppClass.sharedPref.getString("PREV_SURAH_FILEPATH", "") ?: ""
        val title = File(filePath).nameWithoutExtension

        return if (url.isNotEmpty() && filePath.isNotEmpty() && URLUtil.isValidUrl(url)) {
            Triple(url, title, filePath)
        } else {
            null
        }
    }
}