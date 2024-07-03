package com.network.network

import org.json.JSONObject
import retrofit2.Response

abstract class BaseApiResponse {
    private val TAG = "BaseApiResponseTAG"


    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            error(detectError(response))
        } catch (e: Exception) {
            error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)

    private fun <T> detectError(response: Response<T>): String {
        return response.errorBody()?.string()?.let { errorBody ->
            try {
                val json = JSONObject(errorBody)
                if (json.has("message")) {
                    json.getString("message")
                } else {
                    errorBody // Return the raw error body if "message" is not found
                }
            } catch (e: Exception) {
                errorBody // Return the raw error body if parsing fails
            }
        } ?: "Unknown error"
    }


}