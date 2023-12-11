package com.network.models

data class ModelToday(
    var status: Int = 0,
    var message: String = "",
    var `data`: Data = Data()
) {
    data class Data(
        var daily_task: String = "",
        var quran: Quran = Quran(),
        var hadith: Hadith = Hadith()
    ) {
        data class Quran(
            var name: String = "",
            var transliteration_en: String = "",
            var translation_en: String = "",
            var total_verses: String = "",
            var revelation_type: String = "",
            var verse_number: String = "",
            var text: String = "",
            var quran_transliteration_en: String = "",
            var quran_translation_en: String = ""
        )

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
    }
}