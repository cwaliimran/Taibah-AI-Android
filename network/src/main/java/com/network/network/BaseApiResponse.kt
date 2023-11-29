package com.network.network

import android.content.Intent
import android.util.Log
import com.network.utils.AppClass
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class BaseApiResponse {
    private val TAG = "BaseApiResponseTAG"
    private val defaultErrorMessage =
        "Sorry, that doesn't look right. We’re working on fixing it. Please try again in sometime."
    private val defaultConnectionErrorMessage =
        "Looks like you're offline. Please reconnect and refresh to continue."

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return error(detectError(response))
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)

    private fun <T> detectError(response: Response<T>): String {
        return when (response.code()) {
            403 -> getApiError(mapError(NetworkErrors.Forbidden, response.code()))
            404 -> getApiError(mapError(NetworkErrors.NotFound, response.code()))
            502 -> getApiError(mapError(NetworkErrors.BadGateway, response.code()))
            504 -> getApiError(mapError(NetworkErrors.NoInternet, response.code()))
//            in 400..500 -> getApiError(
//                mapError(
//                    NetworkErrors.InternalServerError(response.errorBody()?.string()),
//                    response.code()
//                )
//            )
            in 400..500 -> return handleErrorResponse(response.errorBody())

            -1009 -> getApiError(mapError(NetworkErrors.NoInternet, response.code()))
            -1001 -> getApiError(mapError(NetworkErrors.RequestTimedOut, response.code()))
            else -> {
                getApiError(mapError(NetworkErrors.UnknownError(), response.code()))
            }
        }
    }


    private fun getApiError(error: ServerError): String {
        return error.message ?: defaultErrorMessage
    }


    private fun mapError(error: NetworkErrors, code: Int = 0): ServerError {
        return when (error) {

            is NetworkErrors.NoInternet -> ServerError(
                code,
                defaultConnectionErrorMessage
            )
            is NetworkErrors.RequestTimedOut -> ServerError(
                code,
                defaultConnectionErrorMessage
            )
            is NetworkErrors.BadGateway -> ServerError(code, defaultErrorMessage)
            is NetworkErrors.NotFound -> ServerError(code, defaultErrorMessage)
            is NetworkErrors.Forbidden -> ServerError(
                code,
                "You don't have access to this information"
            )
            is NetworkErrors.InternalServerError -> getError(code, error.response)
            is NetworkErrors.UnknownError -> ServerError(code, defaultErrorMessage)
        }
    }

    private fun getError(code: Int, response: String?): ServerError {
        response?.let {
            if (it.isNotBlank()) {
                try {
                    val obj = JSONObject(it)
//                    Log.d(TAG, "getError: ${obj.toString()}")
//                    Log.d(TAG, "getError message: ${obj.getString("message")}")
                    if (obj.has("message")){
                        return  ServerError(code, obj.getString("message"))
                    }
                    if (obj.has("errors")) {
                        val errors = obj.getJSONArray("errors")
                        Log.d(TAG, "getError: ${errors.toString()}")
                        if (errors.length() > 0) {
                            val message = errors.getJSONObject(0).getString("message")
                            val actualCode = errors.getJSONObject(0).getString("code")
                            return if (message != "null") {
                                ServerError(
                                    code,
                                    errors.getJSONObject(0).getString("message"),
                                    actualCode
                                )
                            } else {
                                ServerError(code, defaultErrorMessage, actualCode)
                            }
                        }
                    } else if (obj.has("error")) {
                        // most probably.. unauthorised error
                        val error = obj.getString("error") ?: defaultErrorMessage
                        if (error.contains("unauthorized", true)) {
                            AppClass.sharedPref.clearAllPreferences()
                            try {
                                // TODO: confirm package name 
                                val intent = Intent(
                                    AppClass.instance,
                                    Class.forName("com.taibahai.activities.LoginActivity")
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                AppClass.instance.startActivity(intent)
                            } catch (e: ClassNotFoundException) {
                                e.printStackTrace()
                            }

                            return ServerError(0, defaultErrorMessage)
                        }
                        return ServerError(0, error)
                    }
                } catch (e: JSONException) {
                    ServerError(code, defaultErrorMessage)
                }
            }
        }
        return ServerError(getDefaultCode(), defaultErrorMessage)
    }


    private fun getDefaultCode(): Int {
        return 0
    }

    data class ServerError(val code: Int?, val message: String?, val actualCode: String = "-1")


    sealed class NetworkErrors {
        object NoInternet : NetworkErrors()
        object RequestTimedOut : NetworkErrors()
        object BadGateway : NetworkErrors()
        object NotFound : NetworkErrors()
        object Forbidden : NetworkErrors()
        class InternalServerError(val response: String?) : NetworkErrors()
        open class UnknownError : NetworkErrors()
    }
}