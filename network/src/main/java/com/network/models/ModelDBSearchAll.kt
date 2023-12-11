package com.network.models

data class ModelDBSearchAll(
    var status: Int = 0,
    var message: String = "",
    var `data`: Data = Data()
) {
    data class Data(
        var hadith: List<Hadith> = listOf(),
        var quran: List<Any> = listOf(),
        var islam_basics: List<IslamBasic> = listOf(),
        var duas: List<Any> = listOf()
    ) {
        data class Hadith(
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

        data class IslamBasic(
            var title: String = "",
            var description: String = "",
            var audio: String = ""
        )
    }
}