package com.taibahai.utils

import android.content.Context
import androidx.annotation.RawRes
import com.taibahai.R
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Scanner

object AppJsonUtils {

    // Read raw files method
    fun readRawResource(context: Context, @RawRes res: Int): String {
        return readStream(context.resources.openRawResource(res))
    }

    private fun readStream(`is`: InputStream): String {
        Scanner(`is`).useDelimiter("\\A").use {
            return if (it.hasNext()) it.next() else ""
        }
    }

    fun loadJSONFromAsset(context: Context): String? {
        var json: String
        try {
            context.resources.openRawResource(R.raw.allsurahlist).use { `is` ->
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                json = String(buffer, StandardCharsets.UTF_8)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun loadQuranJson(context: Context, id: String): String? {
        var json: String
        try {
            context.resources.openRawResource(getFileName(id)).use { `is` ->
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                json = String(buffer, StandardCharsets.UTF_8)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun getFileName(id: String): Int {
        val surahId = id.toIntOrNull() ?: -1
        return when {
            surahId in 1..2 -> R.raw.quran_part_0
            surahId in 3..4 -> R.raw.quran_part_1
            surahId in 5..8 -> R.raw.quran_part_2
            surahId in 9..16 -> R.raw.quran_part_3
            surahId in 17..24 -> R.raw.quran_part_4
            surahId in 25..32 -> R.raw.quran_part_5
            surahId in 33..40 -> R.raw.quran_part_6
            surahId in 41..52 -> R.raw.quran_part_7
            surahId in 53..64 -> R.raw.quran_part_8
            surahId in 65..80 -> R.raw.quran_part_9
            surahId in 81..114 -> R.raw.quran_part_10
            else -> -1
        }
    }
}