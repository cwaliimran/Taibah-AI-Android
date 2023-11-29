package com.network.repository

import android.util.Log
import com.google.gson.JsonObject
import com.network.models.ModelHome
import com.network.models.ModelPrivacyTerms
import com.network.models.ModelProfile
import com.network.models.ModelUploadFile
import com.network.models.ModelUser
import com.network.network.ApiClient
import com.network.network.ApiInterface
import com.network.network.BaseApiResponse
import com.network.network.NetworkResult
import com.network.network.SimpleResponse
import com.network.network.SingleLiveEvent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class MainRepo : BaseApiResponse() {
    private val apiService: ApiInterface by lazy {
        ApiClient.getInstance()!!.create(ApiInterface::class.java)
    }


    val simpleResponseMutableLiveData: SingleLiveEvent<NetworkResult<SimpleResponse>> by lazy {
        SingleLiveEvent()
    }

    suspend fun login(email: String) {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        val jsonObj = JsonObject()
        jsonObj.addProperty("email", email)
        simpleResponseMutableLiveData.postValue(safeApiCall { apiService.login(jsonObj) })
    }


    val verifyOTPMutableLiveData: SingleLiveEvent<NetworkResult<ModelUser>> by lazy {
        SingleLiveEvent()
    }

    suspend fun verifyOtp(email: String, otp: Int) {
        verifyOTPMutableLiveData.value = null
        verifyOTPMutableLiveData.postValue(NetworkResult.Loading())
        val jsonObj = JsonObject()
        jsonObj.addProperty("email", email)
        jsonObj.addProperty("otp", otp)
        verifyOTPMutableLiveData.postValue(safeApiCall { apiService.verifyOtp(jsonObj) })
    }


    suspend fun resendOtp(email: String) {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        val jsonObj = JsonObject()
        jsonObj.addProperty("email", email)
        simpleResponseMutableLiveData.postValue(safeApiCall { apiService.resendOtp(jsonObj) })
    }

    val uploadFileMutableLiveData: SingleLiveEvent<NetworkResult<ModelUploadFile>> by lazy { SingleLiveEvent() }

    suspend fun uploadFile(url: String) {
        uploadFileMutableLiveData.value = null
        uploadFileMutableLiveData.postValue(NetworkResult.Loading())
        try {
            var body: MultipartBody.Part? = null
            if (url != "") {
                val file = File(url)
                Log.d("TAG", "createProduct: filePath: $url")
                // Create a request body with file and image media type
                val reqFile1: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                body = MultipartBody.Part.createFormData(
                    "url", file.name, reqFile1
                )
            }
            return uploadFileMutableLiveData.postValue(safeApiCall {
                apiService.uploadFile(body!!)
            })
        } catch (e: Exception) {
            uploadFileMutableLiveData.postValue(NetworkResult.Error(e.toString()))
        }
    }


    val homeMutableLiveData: SingleLiveEvent<NetworkResult<ModelHome>> by lazy {
        SingleLiveEvent()
    }

    suspend fun home(page: Int, per_page: Int) {
        homeMutableLiveData.value = null
        homeMutableLiveData.postValue(NetworkResult.Loading())
        homeMutableLiveData.postValue(safeApiCall {
            apiService.home(page, per_page)
        })
    }

    suspend fun support(message: String) {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        val jsonObj = JsonObject()
        jsonObj.addProperty("message", message)
        simpleResponseMutableLiveData.postValue(safeApiCall { apiService.support(jsonObj) })
    }


    val privacyTermsMutableLiveData: SingleLiveEvent<NetworkResult<ModelPrivacyTerms>> by lazy {
        SingleLiveEvent()
    }


    suspend fun privacyPolicy() {
        privacyTermsMutableLiveData.value = null
        privacyTermsMutableLiveData.postValue(NetworkResult.Loading())
        privacyTermsMutableLiveData.postValue(safeApiCall { apiService.privacyPolicy() })
    }

    suspend fun terms() {
        privacyTermsMutableLiveData.value = null
        privacyTermsMutableLiveData.postValue(NetworkResult.Loading())
        privacyTermsMutableLiveData.postValue(safeApiCall { apiService.terms() })
    }

    suspend fun deleteAccount() {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        simpleResponseMutableLiveData.postValue(safeApiCall { apiService.deleteAccount() })
    }


    suspend fun logout(device_id: String) {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        val jsonObj = JsonObject()
        jsonObj.addProperty("device_id", device_id)
        simpleResponseMutableLiveData.postValue(safeApiCall { apiService.logout(jsonObj) })
    }


    val profileMutableLiveData: SingleLiveEvent<NetworkResult<ModelProfile>> by lazy {
        SingleLiveEvent()
    }


    suspend fun profile(id: Int) {
        profileMutableLiveData.value = null
        profileMutableLiveData.postValue(NetworkResult.Loading())
        profileMutableLiveData.postValue(safeApiCall {
            apiService.profile(id)
        })
    }


}