package com.network.repository

import com.network.models.ModelChapterHadith3
import com.network.models.ModelHadithBooks
import com.network.models.ModelHadithChapter2
import com.network.models.ModelHadithDetail4
import com.network.models.ModelNextPrevious
import com.network.network.ApiClient
import com.network.network.ApiInterfaceTaibahIslamic
import com.network.network.BaseApiResponse
import com.network.network.NetworkResult
import com.network.network.SingleLiveEvent
import com.network.network.UrlManager.BASE_URL_AI
import com.network.network.UrlManager.BASE_URL_TAIBAH_ISLAMIC


class MainRepoTaibahIslamic : BaseApiResponse() {
    private val apiService: ApiInterfaceTaibahIslamic by lazy {
        ApiClient.getInstance(BASE_URL_AI)!!
            .create(ApiInterfaceTaibahIslamic::class.java)
    }

    val hadithBooksMutableLiveData: SingleLiveEvent<NetworkResult<ModelHadithBooks>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getHadithBooks(
    ) {
        hadithBooksMutableLiveData.value = null
        hadithBooksMutableLiveData.postValue(NetworkResult.Loading())
        hadithBooksMutableLiveData.postValue(safeApiCall {
            apiService.getHadithBooks1()
        })
    }



    val hadithChapterMutableLiveData: SingleLiveEvent<NetworkResult<ModelHadithChapter2>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getHadithChapters(pageno:Int,book_id:String,total_pages:String ) {
        hadithChapterMutableLiveData.value = null
        hadithChapterMutableLiveData.postValue(NetworkResult.Loading())
        hadithChapterMutableLiveData.postValue(safeApiCall {
            apiService.getHadithChapters2(pageno,book_id,total_pages)
        })
    }



    val chapterHadithMutableLiveData: SingleLiveEvent<NetworkResult<ModelChapterHadith3>> by lazy {
        SingleLiveEvent()
    }

    suspend fun getChapterHadiths(pageno:Int,chapter_id:String ) {
        chapterHadithMutableLiveData.value = null
        chapterHadithMutableLiveData.postValue(NetworkResult.Loading())
        chapterHadithMutableLiveData.postValue(safeApiCall {
            apiService.getChapterHadiths3(pageno,chapter_id)
        })
    }

    val hadithDetailMutableLiveData: SingleLiveEvent<NetworkResult<ModelHadithDetail4>> by lazy {
        SingleLiveEvent()
    }


    suspend fun getHadithDetail(id:String ) {
        hadithDetailMutableLiveData.value = null
        hadithDetailMutableLiveData.postValue(NetworkResult.Loading())
        hadithDetailMutableLiveData.postValue(safeApiCall {
            apiService.getHadithDetail(id)
        })
    }


    val nextPreviousMutableLiveData: SingleLiveEvent<NetworkResult<ModelNextPrevious>> by lazy {
        SingleLiveEvent()
    }
    suspend fun nextPreviousHadith(chapter_id: String,hadith_id:String,isNext:Boolean ) {
        nextPreviousMutableLiveData.value = null
        nextPreviousMutableLiveData.postValue(NetworkResult.Loading())
        nextPreviousMutableLiveData.postValue(safeApiCall {
            apiService.nextPreviousHadith(chapter_id,hadith_id,isNext)
        })
    }

}