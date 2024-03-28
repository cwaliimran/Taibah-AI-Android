package com.network.network

import android.content.Intent
import com.network.utils.AppClass
import com.network.utils.AppConstants
import okhttp3.ResponseBody
import org.json.JSONObject


fun handleErrorResponse(errorBody: ResponseBody?): String {
    val serverErrorsList: List<String> =
        listOf("unauthorized", "invalid signature", "jwt expired", "jwt malformed")
    val errorMsg = try {
        val jObjError = errorBody?.string()?.let { JSONObject(it) }
        jObjError?.getString("message")

    } catch (e: java.lang.Exception) {
        e.message.toString()
    }
    if (serverErrorsList.contains(errorMsg?.lowercase())) {
        val aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)
        AppClass.sharedPref.clearAllPreferences()
        AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
        AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)
        try {
            val intent = Intent(
                AppClass.instance,
                Class.forName("com.taibahai.activities.LoginActivity")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            AppClass.instance.startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
    return errorMsg.toString()
}