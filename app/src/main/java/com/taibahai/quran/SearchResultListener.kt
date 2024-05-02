package com.taibahai.quran

import com.network.models.ModelChapter

interface SearchResultListener {
    fun onSearchResultFound(result: ModelChapter)
    fun onProgressUpdate(surahId: Int){}
    fun onSearchComplete()

}