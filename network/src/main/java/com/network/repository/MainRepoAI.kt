package com.network.repository

import android.util.Log
import com.network.models.ModelBooks
import com.network.models.ModelDailyAlert
import com.network.models.ModelGetFeeds
import com.network.models.ModelHome
import com.network.models.ModelInheritanceLaw
import com.network.models.ModelPostFeed
import com.network.models.ModelPrivacyTerms
import com.network.models.ModelScholars
import com.network.models.ModelToday
import com.network.models.ModelUpcoming
import com.network.models.ModelUploadFile
import com.network.models.ModelUser
import com.network.network.ApiClient
import com.network.network.ApiInterfaceAI
import com.network.network.BaseApiResponse
import com.network.network.NetworkResult
import com.network.network.SimpleResponse
import com.network.network.SingleLiveEvent
import com.network.network.UrlManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class MainRepoAI : BaseApiResponse() {
    private val apiService: ApiInterfaceAI by lazy {
        ApiClient.getInstance(UrlManager.BASE_URL_AI)!!.create(ApiInterfaceAI::class.java)
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


    suspend fun postProfile(name:String, image:String) {
        socialLoginMutableLiveData.value = null
        socialLoginMutableLiveData.postValue(NetworkResult.Loading())
        socialLoginMutableLiveData.postValue(safeApiCall {
            apiService.postProfile(name,image)
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

    suspend fun feedComment(postId: String, comment: String)
    {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        simpleResponseMutableLiveData.postValue(safeApiCall {
            apiService.feedComment(postId,comment)
        })
    }


    suspend fun putLike(feedId: String)
    {
        simpleResponseMutableLiveData.value = null
        simpleResponseMutableLiveData.postValue(NetworkResult.Loading())
        simpleResponseMutableLiveData.postValue(safeApiCall {
            apiService.putLike(feedId)
        })
    }



    val getFeedMutableLiveData: SingleLiveEvent<NetworkResult<ModelGetFeeds>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getFeed(feedId: String)
    {
        getFeedMutableLiveData.value = null
        getFeedMutableLiveData.postValue(NetworkResult.Loading())
        getFeedMutableLiveData.postValue(safeApiCall {
            apiService.getFeed(feedId)
        })
    }

    suspend fun feedReport(feedId:String)
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

    val inheritanceLawMutableLiveData: SingleLiveEvent<NetworkResult<ModelInheritanceLaw>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getInheritanceLaw() {
        inheritanceLawMutableLiveData.value = null
        inheritanceLawMutableLiveData.postValue(NetworkResult.Loading())
        inheritanceLawMutableLiveData.postValue(safeApiCall {
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




    val dbSearchMutableLiveData: SingleLiveEvent<NetworkResult<Any>> by lazy {
        SingleLiveEvent()
    }

    suspend fun dbSearch(type: String,keyword:String)
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

    val todayMutableLiveData: SingleLiveEvent<NetworkResult<ModelToday>> by lazy {
        SingleLiveEvent()
    }

    suspend fun today()
    {
        todayMutableLiveData.value = null
        todayMutableLiveData.postValue(NetworkResult.Loading())
        todayMutableLiveData.postValue(safeApiCall {
            apiService.today()
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