package com.network.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.network.models.ModelHome
import com.network.models.ModelPrivacyTerms
import com.network.models.ModelProfile
import com.network.models.ModelUploadFile
import com.network.models.ModelUser
import com.network.network.NetworkResult
import com.network.network.SimpleResponse
import com.network.repository.MainRepo
import kotlinx.coroutines.launch

class MainViewModel constructor(
    application: Application
) : AndroidViewModel(application) {
    private val repository: MainRepo by lazy {
        MainRepo()
    }

    val simpleResponseLiveData: MutableLiveData<NetworkResult<SimpleResponse>>
        get() = repository.simpleResponseMutableLiveData

    fun login(email: String) {
        viewModelScope.launch {
            repository.login(email)
        }
    }

    val verifyOtpLiveData: MutableLiveData<NetworkResult<ModelUser>>
        get() = repository.verifyOTPMutableLiveData

    fun verifyOtp(email: String, otp: Int) {
        viewModelScope.launch {
            repository.verifyOtp(email, otp)
        }
    }


    fun resendOtp(email: String) {
        viewModelScope.launch {
            repository.resendOtp(email)
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


    val homeLiveData: MutableLiveData<NetworkResult<ModelHome>>
        get() = repository.homeMutableLiveData

    fun home(page: Int, per_page: Int) {
        viewModelScope.launch {
            repository.home(page, per_page)
        }
    }


    fun support(message: String) {
        viewModelScope.launch {
            repository.support(message)
        }
    }


    val privacyTermsLiveData: MutableLiveData<NetworkResult<ModelPrivacyTerms>>
        get() = repository.privacyTermsMutableLiveData

    fun privacyPolicy() {
        viewModelScope.launch {
            repository.privacyPolicy()
        }
    }

    fun terms() {
        viewModelScope.launch {
            repository.terms()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            repository.deleteAccount()
        }
    }

    fun logout(device_id: String) {
        viewModelScope.launch {
            repository.logout(device_id)
        }
    }


    val profileLiveData: MutableLiveData<NetworkResult<ModelProfile>>
        get() = repository.profileMutableLiveData

    fun profile(id: Int) {
        viewModelScope.launch {
            repository.profile(id)
        }
    }

}