package com.network.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.network.models.ModelBooks
import com.network.models.ModelDBSearch
import com.network.models.ModelDailyAlert
import com.network.models.ModelGetFeeds
import com.network.models.ModelHome
import com.network.models.ModelPostFeed
import com.network.models.ModelPrivacyTerms
import com.network.models.ModelProfile
import com.network.models.ModelScholars
import com.network.models.ModelUpcoming
import com.network.models.ModelUploadFile
import com.network.models.ModelUser
import com.network.network.NetworkResult
import com.network.network.SimpleResponse
import com.network.repository.MainRepo
import kotlinx.coroutines.launch

class MainViewModel constructor(application: Application) : AndroidViewModel(application) {
    private val repository: MainRepo by lazy {
        MainRepo()
    }

    val simpleResponseLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
        get() = repository.simpleResponseMutableLiveData

    val socialLoginLiveData: MutableLiveData<NetworkResult<ModelUser>>
        get() = repository.socialLoginMutableLiveData

    fun socialLogin(social_id:String,
                    social_type: String,
                    device_id: String,
                    device_type: String,
                    email: String,
                    timezone: String,
                    name: String,
                    image: String) {
        viewModelScope.launch {
            repository.socialLogin(social_id,social_type,device_id,device_type,email,timezone,name,image)
        }
    }


    fun profile() {
        viewModelScope.launch {
            repository.profile()
        }
    }


    val postFeedLiveData: MutableLiveData<NetworkResult<ModelPostFeed>>
        get() = repository.postFeedMutableLiveData

    fun postFeed(description:String, type: String )
    {
        viewModelScope.launch {
            repository.postFeed(description,type)
        }
    }


    fun feedComment(postId:Int, comment: String )
    {
        viewModelScope.launch {
            repository.feedComment(postId,comment)
        }
    }


    val getFeedLiveData: MutableLiveData<NetworkResult<ModelGetFeeds>>
        get() = repository.getFeedMutableLiveData

    fun getFeed(feedId:Int)
    {
        viewModelScope.launch {
            repository.getFeed(feedId)
        }
    }


    fun feedReport(feedId:Int)
    {
        viewModelScope.launch {
            repository.feedReport(feedId)
        }
    }


    val homeLiveData: MutableLiveData<NetworkResult<ModelHome>>
        get() = repository.homeMutableLiveData

    fun home(pageno:Int) {
        viewModelScope.launch {
            repository.home(pageno)
        }
    }


    fun support(subject: String,message: String)
    {
        viewModelScope.launch {
            repository.support(subject,message)
        }
    }


    val booksLiveData: MutableLiveData<NetworkResult<ModelBooks>>
        get() = repository.booksMutableLiveData

    fun books() {
        viewModelScope.launch {
            repository.books()
        }
    }


    val scholarsLiveData: MutableLiveData<NetworkResult<ModelScholars>>
        get() = repository.scholarsMutableLiveData

    fun scholars() {
        viewModelScope.launch {
            repository.scholars()
        }
    }



    val aboutILPrivacyTermLiveData: MutableLiveData<NetworkResult<ModelPrivacyTerms>>
        get() = repository.aboutILPrivacyTermMutableLiveData

    fun getInheritanceLaw() {
        viewModelScope.launch {
            repository.getInheritanceLaw()
        }
    }


    fun about() {
        viewModelScope.launch {
            repository.about()
        }
    }


    fun privacy() {
        viewModelScope.launch {
            repository.privacy()
        }
    }


    fun terms() {
        viewModelScope.launch {
            repository.terms()
        }
    }


    val dbSearchLiveData: MutableLiveData<NetworkResult<ModelDBSearch>>
        get() = repository.dbSearchMutableLiveData

    fun dbSearch(type:String, keyword: String )
    {
        viewModelScope.launch {
            repository.dbSearch(type,keyword)
        }
    }


    val uploadFileLiveData: LiveData<NetworkResult<ModelUploadFile>>
        get() = repository.uploadFileMutableLiveData


    // UPLOAD FILE AND IMAGE
    fun uploadFile(filePath: String? = "") {
        viewModelScope.launch {
            if (!filePath.isNullOrEmpty()) {
                try {
                    repository.uploadFile(filePath)
                } catch (e: Exception) {
                }
            }
        }
    }



    val dailyAlertLiveData: MutableLiveData<NetworkResult<ModelDailyAlert>>
        get() = repository.dailyAlertMutableLiveData

    fun dailyAlert()
    {
        viewModelScope.launch {
            repository.dailyAlert()
        }
    }


    val upcomingLiveData: MutableLiveData<NetworkResult<ModelUpcoming>>
        get() = repository.upcomingMutableLiveData

    fun upcoming()
    {
        viewModelScope.launch {
            repository.upcoming()
        }
    }



}