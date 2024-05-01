package com.taibahai.utils

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.network.models.ModelChapter
import com.taibahai.R
import com.taibahai.quran.SearchResultListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.text.Normalizer
import java.util.Scanner
import java.util.regex.Pattern

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
            context.resources.openRawResource(getFileName(id.toInt())).use { `is` ->
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

    private fun getFileName(surahId: Int): Int {
        return when (surahId) {
            in 1..2 -> R.raw.quran_part_0
            in 3..4 -> R.raw.quran_part_1
            in 5..8 -> R.raw.quran_part_2
            in 9..16 -> R.raw.quran_part_3
            in 17..24 -> R.raw.quran_part_4
            in 25..32 -> R.raw.quran_part_5
            in 33..40 -> R.raw.quran_part_6
            in 41..52 -> R.raw.quran_part_7
            in 53..64 -> R.raw.quran_part_8
            in 65..80 -> R.raw.quran_part_9
            in 81..114 -> R.raw.quran_part_10
            else -> -1
        }
    }

    fun loadQuranJson(context: Context, startSurahId: Int, endSurahId: Int): List<String?> {
        val jsonList = mutableListOf<String?>()
        for (id in startSurahId..endSurahId) {
            val json = loadQuranJson(context, id.toString())
            json?.let { jsonList.add(it) }
        }
        return jsonList
    }

    private val searchScope = CoroutineScope(Dispatchers.Default)
    private var searchJob: Job? = null


    fun searchWholeQuran(
        searchText: String,
        context: Context,
        listener: SearchResultListener? = null,
        isCancelJob: Boolean? = false
    ) {
        searchJob?.cancel(null)
// TODO: job cancellation
        searchJob = searchScope.launch {
            val startSurahId = 1 // ID of the first Surah
            val endSurahId = 114 // ID of the last Surah
            val jsonList = loadQuranJson(context, startSurahId, endSurahId)
            val processedIds = HashSet<String>() // Set to store processed record IDs

            for ((index, json) in jsonList.withIndex()) {               
                json?.let { quranJson ->
                    if (isCancelJob == true) {
                        Log.d("TAG", "searchWholeQuran: LET CANCELLED")
                        return@let
                    }
                    try {
                        val jsonArr = JSONArray(quranJson)
                        for (i in 0 until jsonArr.length()) {
                            val surahModel = ModelChapter()
                            surahModel.surah_number =
                                jsonArr.getJSONObject(i).getString("surah_number")
                            surahModel.verse_number =
                                jsonArr.getJSONObject(i).getString("verse_number")
                            surahModel.text = jsonArr.getJSONObject(i).getString("text")
                            surahModel.translation_en =
                                jsonArr.getJSONObject(i).getString("translation_en")
                            surahModel.transliteration_en =
                                jsonArr.getJSONObject(i).getString("transliteration_en")
                            // Perform search within each Surah's JSON data
                            val transliterationEnWithoutHtmlTags =
                                removeHtmlTags(surahModel.transliteration_en)

                            // Check if the record has already been processed
                            val recordId = "${surahModel.surah_number}-${surahModel.verse_number}"
                            if (!processedIds.contains(recordId) && (containsIgnoreCase(
                                    surahModel.surah_number,
                                    searchText
                                ) || containsIgnoreCase(
                                    surahModel.verse_number,
                                    searchText
                                ) || containsIgnoreCase(
                                    surahModel.text,
                                    searchText
                                ) || containsIgnoreCase(
                                    surahModel.transliteration_en, searchText
                                ) || containsIgnoreCase(
                                    transliterationEnWithoutHtmlTags, searchText
                                ) || containsIgnoreCase(surahModel.id, searchText))
                            ) {
                                // Match found, notify the listener
                                listener?.onSearchResultFound(surahModel)
                                processedIds.add(recordId) // Add the processed record ID to the set
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
            // If no results are found, call onSearchComplete
            Log.d("TAG", "searchWholeQuran: onSearchComplete")
            listener?.onSearchComplete()
        }
    }


    // Function to remove HTML tags from a string
    fun removeHtmlTags(text: String): String {
        return text.replace(Regex("<[/]?[uUbB]>"), "") // Remove <u> and </U> tags
    }

    // a function to normalize Arabic text
    fun normalizeArabic(text: String): String {
        val normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKD)
        val pattern = Pattern.compile("\\p{Mn}")
        return pattern.matcher(normalizedText).replaceAll("")
    }

    // Function to check if a string contains a substring without case sensitivity
    fun containsIgnoreCase(text: String, search: String): Boolean {
        val normalizedText = normalizeArabic(text)
        val normalizedSearch = normalizeArabic(search)
        return normalizedText.contains(normalizedSearch, ignoreCase = true)
    }

}