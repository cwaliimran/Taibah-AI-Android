package com.taibahai.utils

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.Executors


interface DownloadListener {
    fun onDownloadComplete(file: File)
    fun onDownloadFailed(error: String)
}

class DownloadMusicFile(private val context: Context, param: DownloadListener) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val TAG = "DownloadMusicFile"
    fun downloadMusicFile(musicURL: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            var count: Int
            try {
                val url = URL(musicURL)
                val connection: URLConnection = url.openConnection()
                connection.connect()
                val input: InputStream = BufferedInputStream(url.openStream(), 8192)
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                    "hassan1.mp3"
                )
                val output: FileOutputStream = FileOutputStream(file)

                val data = ByteArray(1024)

                while (input.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                // Run UI updates on the main thread using Handler
                mainHandler.post {
                    Toast.makeText(context, "Music Download complete.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "downloadMusicFile: completed")
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }
}