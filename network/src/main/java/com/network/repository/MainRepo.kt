package com.network.repository

import android.util.Log
import com.network.models.ModelBooks
import com.network.models.ModelDBSearch
import com.network.models.ModelDailyAlert
import com.network.models.ModelGetFeeds
import com.network.models.ModelHome
import com.network.models.ModelPostFeed
import com.network.models.ModelPrivacyTerms
import com.network.models.ModelScholars
import com.network.models.ModelUpcoming
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



    val socialLoginMutableLiveData: SingleLiveEvent<NetworkResult<ModelUser>> by lazy {
        SingleLiveEvent()
    }

    suspend fun socialLogin(
        social_id: String,
        social_type: String,
        device_id: String,
        device_type: String,
        email: String,
        timezone: String,
        name: String,
        image: String
    ) {
        socialLoginMutableLiveData.value = null
        socialLoginMutableLiveData.postValue(NetworkResult.Loading())
        socialLoginMutableLiveData.postValue(safeApiCall {
            apiService.socialLogin(
                social_id, social_type,device_id,device_type,email,timezone,name,image
            )
        })
    }



    suspend fun profile() {
        socialLoginMutableLiveData.value = null
        socialLoginMutableLiveData.postValue(NetworkResult.Loading())
        socialLoginMutableLiveData.postValue(safeApiCall {
            apiService.profile()
        })
    }



    val postFeedMutableLiveData: SingleLiveEvent<NetworkResult<ModelPostFeed>> by lazy {
        SingleLiveEvent()
    }

    suspend fun postFeed(description: String,file: String,)
    {
        postFeedMutableLiveData.value = null
        postFeedMutableLiveData.postValue(NetworkResult.Loading())
        postFeedMutableLiveData.postValue(safeApiCall {
            apiService.postFeed(description, file)
        })
    }


    val simpleResponseMutableLiveData: SingleLiveEvent<NetworkResult<SimpleResponse>> by lazy {
        SingleLiveEvent()
    }

    suspend fun feedComment(postId: Int,comment: String)
    {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        simpleResponseMutableLiveData.postValue(safeApiCall {
            apiService.feedComment(postId,comment)
        })
    }




    val getFeedMutableLiveData: SingleLiveEvent<NetworkResult<ModelGetFeeds>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getFeed(feedId:Int)
    {
        getFeedMutableLiveData.value = null
        getFeedMutableLiveData.postValue(NetworkResult.Loading())
        getFeedMutableLiveData.postValue(safeApiCall {
            apiService.getFeed(feedId)
        })
    }

    suspend fun feedReport(feedId:Int)
    {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        simpleResponseMutableLiveData.postValue(safeApiCall {
            apiService.feedReport(feedId)
        })
    }


    val homeMutableLiveData: SingleLiveEvent<NetworkResult<ModelHome>> by lazy {
        SingleLiveEvent()
    }

    suspend fun home(pageno: Int) {
        homeMutableLiveData.value = null
        homeMutableLiveData.postValue(NetworkResult.Loading())
        homeMutableLiveData.postValue(safeApiCall {
            apiService.home(pageno)
        })
    }




    suspend fun support(subject: String,message: String)
    {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        simpleResponseMutableLiveData.postValue(safeApiCall {
            apiService.support(subject, message)
        })
    }



    val booksMutableLiveData: SingleLiveEvent<NetworkResult<ModelBooks>> by lazy {
        SingleLiveEvent()
    }

    suspend fun books() {
        booksMutableLiveData.value = null
        booksMutableLiveData.postValue(NetworkResult.Loading())
        booksMutableLiveData.postValue(safeApiCall {
            apiService.books()
        })
    }

    val scholarsMutableLiveData: SingleLiveEvent<NetworkResult<ModelScholars>> by lazy {
        SingleLiveEvent()
    }

    suspend fun scholars() {
        scholarsMutableLiveData.value = null
        scholarsMutableLiveData.postValue(NetworkResult.Loading())
        scholarsMutableLiveData.postValue(safeApiCall {
            apiService.scholars()
        })
    }


    val aboutILPrivacyTermMutableLiveData: SingleLiveEvent<NetworkResult<ModelPrivacyTerms>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getInheritanceLaw() {
        aboutILPrivacyTermMutableLiveData.value = null
        aboutILPrivacyTermMutableLiveData.postValue(NetworkResult.Loading())
        aboutILPrivacyTermMutableLiveData.postValue(safeApiCall {
            apiService.getInheritanceLaw()
        })
    }


    suspend fun about() {
        aboutILPrivacyTermMutableLiveData.value = null
        aboutILPrivacyTermMutableLiveData.postValue(NetworkResult.Loading())
        aboutILPrivacyTermMutableLiveData.postValue(safeApiCall {
            apiService.about()
        })
    }

    suspend fun privacy() {
        aboutILPrivacyTermMutableLiveData.value = null
        aboutILPrivacyTermMutableLiveData.postValue(NetworkResult.Loading())
        aboutILPrivacyTermMutableLiveData.postValue(safeApiCall {
            apiService.privacy()
        })
    }

    suspend fun terms() {
        aboutILPrivacyTermMutableLiveData.value = null
        aboutILPrivacyTermMutableLiveData.postValue(NetworkResult.Loading())
        aboutILPrivacyTermMutableLiveData.postValue(safeApiCall {
            apiService.terms()
        })
    }




    val dbSearchMutableLiveData: SingleLiveEvent<NetworkResult<ModelDBSearch>> by lazy {
        SingleLiveEvent()
    }

    suspend fun dbSearch(type: String,keyword: String,)
    {
        dbSearchMutableLiveData.value = null
        dbSearchMutableLiveData.postValue(NetworkResult.Loading())
        dbSearchMutableLiveData.postValue(safeApiCall {
            apiService.dbSearch(type, keyword)
        })
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
                    "file", file.name, reqFile1
                )
            }
            return uploadFileMutableLiveData.postValue(safeApiCall {
                apiService.uploadFile(body!!)
            })
        } catch (e: Exception) {
            uploadFileMutableLiveData.postValue(NetworkResult.Error(e.toString()))
        }
    }



    val dailyAlertMutableLiveData: SingleLiveEvent<NetworkResult<ModelDailyAlert>> by lazy {
        SingleLiveEvent()
    }

    suspend fun dailyAlert()
    {
        dailyAlertMutableLiveData.value = null
        dailyAlertMutableLiveData.postValue(NetworkResult.Loading())
        dailyAlertMutableLiveData.postValue(safeApiCall {
            apiService.dailyAlert()
        })
    }


    val upcomingMutableLiveData: SingleLiveEvent<NetworkResult<ModelUpcoming>> by lazy {
        SingleLiveEvent()
    }

    suspend fun upcoming()
    {
        upcomingMutableLiveData.value = null
        upcomingMutableLiveData.postValue(NetworkResult.Loading())
        upcomingMutableLiveData.postValue(safeApiCall {
            apiService.upcoming()
        })
    }



}