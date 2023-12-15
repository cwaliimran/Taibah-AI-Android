package com.network.models

data class ModelHadithChapter2(
    var status: Int = 0,
    var message: String = "",
    var total_pages: Int = 0,
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var book_id: String = "",
        var chapter_name: String = "",
        var arabic_name: String = "",
        var hadith_number: String = "",
        var order_: String = "",
        var total_hadith: String = ""
    )
}