package com.network.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.network.models.ModelChapterHadith3
import com.network.models.ModelHadithBooks
import com.network.models.ModelHadithChapter2
import com.network.models.ModelHadithDetail4
import com.network.models.ModelNextPrevious
import com.network.network.NetworkResult
import com.network.repository.MainRepoTaibahIslamic
import kotlinx.coroutines.launch

class MainViewModelTaibahIslamic constructor(application: Application) :
    AndroidViewModel(application) {
    private val repository: MainRepoTaibahIslamic by lazy {
        MainRepoTaibahIslamic()
    }


    val hadithBooksLiveData: MutableLiveData<NetworkResult<ModelHadithBooks>>
        get() = repository.hadithBooksMutableLiveData

    fun getHadithBooks() {
        viewModelScope.launch {
            repository.getHadithBooks()
        }
    }


    val hadithChapterLiveData: MutableLiveData<NetworkResult<ModelHadithChapter2>>
        get() = repository.hadithChapterMutableLiveData

    fun getHadithChapters(pageno:Int, book_id:String, total_pages:String) {
        viewModelScope.launch {
            repository.getHadithChapters(pageno,book_id,total_pages)
        }
    }


    val chapterHadithLiveData: MutableLiveData<NetworkResult<ModelChapterHadith3>>
        get() = repository.chapterHadithMutableLiveData

    fun getChapterHadiths(pageno:Int, chapter_id:String) {
        viewModelScope.launch {
            repository.getChapterHadiths(pageno,chapter_id)
        }
    }

    val hadithDetailLiveData: MutableLiveData<NetworkResult<ModelHadithDetail4>>
        get() = repository.hadithDetailMutableLiveData


    fun getHadithDetail(id:String) {
        viewModelScope.launch {
            repository.getHadithDetail(id)
        }
    }

    val nextPreviouslLiveData: MutableLiveData<NetworkResult<ModelNextPrevious>>
        get() = repository.nextPreviousMutableLiveData

    fun nextPreviousHadith(chapter_id: String,hadith_id:String,isNext:Boolean ) {
        viewModelScope.launch {
            repository.nextPreviousHadith(chapter_id,hadith_id,isNext)
        }
    }

}