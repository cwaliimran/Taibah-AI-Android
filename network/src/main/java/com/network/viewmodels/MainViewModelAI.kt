package com.network.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.network.models.ModelBooks
import com.network.models.ModelChapterHadith3
import com.network.models.ModelDailyAlert
import com.network.models.ModelGetFeeds
import com.network.models.ModelHome
import com.network.models.ModelInheritanceLaw
import com.network.models.ModelNotifications
import com.network.models.ModelPostFeed
import com.network.models.ModelPrivacyTerms
import com.network.models.ModelScholars
import com.network.models.ModelToday
import com.network.models.ModelUpcoming
import com.network.models.ModelUploadFile
import com.network.models.ModelUser
import com.network.network.NetworkResult
import com.network.network.SimpleResponse
import com.network.repository.MainRepoAI
import kotlinx.coroutines.launch

class MainViewModelAI(application: Application) : AndroidViewModel(application) {
    private val repository: MainRepoAI by lazy {
        MainRepoAI()
    }

    val simpleResponseLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
        get() = repository.simpleResponseMutableLiveData

    val likeLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
        get() = repository.likeMutableLiveData

    val deleteFeedLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
            get() = repository.deleteFeedMutableLiveData

    val logoutLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
        get() = repository.logoutMutableLiveData

    val reportFeedLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
        get() = repository.reportFeedMutableLiveData

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


    fun postProfile(name:String, image:String) {
        viewModelScope.launch {
            repository.postProfile(name,image)
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




    fun feedComment(postId:String, comment: String )
    {
        viewModelScope.launch {
            repository.feedComment(postId,comment)
        }
    }

    fun putLike(feedId: String)
    {
        viewModelScope.launch {
            repository.putLike(feedId)
        }
    }

    fun deleteFeed(feedId: String)
    {
        viewModelScope.launch {
            repository.deleteFeed(feedId)
        }
    }



    val getFeedLiveData: MutableLiveData<NetworkResult<ModelGetFeeds>>
        get() = repository.getFeedMutableLiveData

    fun getFeed(feedId: String)
    {
        viewModelScope.launch {
            repository.getFeed(feedId)
        }
    }


    fun feedReport(feedId:String)
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


    val inheritanceLawLiveData: MutableLiveData<NetworkResult<ModelInheritanceLaw>>
        get() = repository.inheritanceLawMutableLiveData
    fun getInheritanceLaw() {
        viewModelScope.launch {
            repository.getInheritanceLaw()
        }
    }



    val aboutILPrivacyTermLiveData: MutableLiveData<NetworkResult<ModelPrivacyTerms>>
        get() = repository.aboutILPrivacyTermMutableLiveData

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


    val dbSearchHadithWithBookIdLiveData: MutableLiveData<NetworkResult<ModelChapterHadith3>>
        get() = repository.dbSearchHadithWithBookId

    fun hadithSearchWithBookId(pageno: Int,search:String, book_number: String )
    {
        viewModelScope.launch {
            repository.hadithSearchWithBookId(pageno,search,book_number)
        }
    }

    val dbSearchHadithAllBooksLiveData: MutableLiveData<NetworkResult<ModelChapterHadith3>>
        get() = repository.dbSearchHadithAllBooks

    fun hadithSearchAllBooks(pageno: Int,search:String )
    {
        viewModelScope.launch {
            repository.hadithSearchAllBooks(pageno,search)
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


    val todayLiveData: MutableLiveData<NetworkResult<ModelToday>>
        get() = repository.todayMutableLiveData

    fun today()
    {
        viewModelScope.launch {
            repository.today()
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
    val notificationsLiveData: MutableLiveData<NetworkResult<ModelNotifications>>
        get() = repository.notificationsMutableLiveData

    fun notifications(pageno: Int)
    {
        viewModelScope.launch {
            repository.notifications(pageno)
        }
    }

    fun logout(device_id: String, device_type: String )
    {
        viewModelScope.launch {
            repository.logout(device_id,device_type)
        }
    }

    fun deleteAccount()
    {
        viewModelScope.launch {
            repository.deleteAccount()
        }
    }


}