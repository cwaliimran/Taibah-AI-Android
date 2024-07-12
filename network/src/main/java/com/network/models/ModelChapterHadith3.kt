package com.network.models

data class ModelChapterHadith3(
    var status: Int = 0,
    var message: String = "",
    var total_pages: Int = 0,
    var data: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var arabic: String = "",
        var english_translation: String = "",
        var urdu: String = "",
        var reference: String = "",
        var hadith_no: String = "",
        var book_name: String = "",
        var type: String = "",
        var is_active: String = "",
        var chapter_id: String = "",
        var chapter_name:String=""
    )
}