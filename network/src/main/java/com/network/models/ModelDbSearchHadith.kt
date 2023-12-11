package com.network.models

data class ModelDbSearchHadith(
    var status: Int = 0,
    var message: String = "",
    var data: List<Data> = listOf()
) {
    data class Data(
        var chapter_name: String = "",
        var id: String = "",
        var arabic: String = "",
        var english_translation: String = "",
        var urdu: String = "",
        var reference: String = "",
        var hadith_no: String = "",
        var is_active: String = "",
        var chapter_id: String = ""
    )
}