package com.taibahai.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/*

 fun getFilePathFormUri(uri: Uri, context: Context): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val file = File(context.filesDir, name)
    return file.path
}

*/


fun getFileNameFormUri(uri: Uri, context: Context): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    returnCursor.use { cursor ->
        val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(nameIndex)
    }

}

fun progressToTimer(i: Int, i2: Int): Int {
    return (i.toDouble() / 100.0 * (i2 / 1000).toDouble()).toInt() * 1000
}



fun getFilePathFormUri(uri: Uri, context: Context): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)

    val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//   val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor?.moveToFirst()
    val name = nameIndex?.let { returnCursor.getString(it) }
    //val size = returnCursor.getLong(sizeIndex).toString()
    val file = name?.let { File(context.filesDir, it) }
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable: Int? = inputStream?.available()

        //int bufferSize = 1024;
        val bufferSize = bytesAvailable?.coerceAtMost(maxBufferSize)
        val buffers = bufferSize?.let { ByteArray(it) }
        while (inputStream?.read(buffers).also {
                if (it != null) {
                    read = it
                }
            } != -1) {
            outputStream.write(buffers, 0, read)
        }
        // Log.e("File Size", "Size " + file.length())
        inputStream?.close()
        outputStream.close()
//      Log.e("File Path", "Path " + file.path)
//      Log.e("File Size", "Size " + file.length())
    } catch (e: java.lang.Exception) {
        //Log.e("Exception", e.message!!)
        returnCursor?.close()
    }
    return file?.path
}