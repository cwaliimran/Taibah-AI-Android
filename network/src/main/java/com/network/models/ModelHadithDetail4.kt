package com.network.models

data class ModelHadithDetail4(
    var status: Int = 0,
    var message: String = "",
    var `data`: Data = Data()
) {
    data class Data(
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