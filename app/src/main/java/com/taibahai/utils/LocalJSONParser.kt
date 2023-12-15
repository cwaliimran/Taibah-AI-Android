package com.cwnextgen.amnames.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.util.Arrays

class LocalJSONParser {
    companion object {
        fun inputStreamToString(inputStream: InputStream): String {
            try {
                val bytes = ByteArray(inputStream.available())
                inputStream.read(bytes, 0, bytes.size)
                return String(bytes)
            } catch (e: IOException) {
                return ""
            }
        }
    }
}

// jsonFileName = "data"
inline fun <reified T> Context.getObjectFromJson(jsonFileName: String): T {
    val myJson = LocalJSONParser.inputStreamToString(this.assets.open(jsonFileName))
    return Gson().fromJson(myJson, T::class.java)
}

//parse array
// val jsonFileString = this.getJsonDataFromAsset("filename")
//        val listType = object : TypeToken<List<ModelClass>>() {}.type
//        var arr: List<ModelClass> = gson.fromJson(jsonFileString, listType)
//        mData.addAll(arr)
fun Context.getJsonDataFromAsset(fileName: String): String? {
    val jsonString: String
    try {
        jsonString = this.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}

fun <T> Context.getArrayFromAssets(fileName: String): MutableList<T> {
    val gson = Gson()
    val listPersonType = object : TypeToken<MutableList<T>>() {}.type
    val jsonFileString = getJsonDataFromAsset(fileName)
    var arr: MutableList<T> = parseArray(jsonFileString!!, listPersonType)
    return arr
}

inline fun <reified T> parseArray(json: String, typeToken: Type): T {
    val gson = GsonBuilder().create()
    return gson.fromJson<T>(json, typeToken)
}

fun <T> Context.parseGsonArray(fileName: String, model: Class<Array<T>?>?): MutableList<Array<T>?> {
    val jsonFileString = getJsonDataFromAsset(fileName)
    return Arrays.asList(Gson().fromJson(jsonFileString, model))
}