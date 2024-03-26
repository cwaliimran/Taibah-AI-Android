package com.taibahai.quran

class SurahModel {
    var position: String? = null
    var catagory_id: String? = null
    var id: String? = null
    var hadiths_number: String? = null
    var imam_name: String? = null
    var hadiths_type: String? = null
    var chapter: String? = null
    var arabicText: String? = null
    var englishText: String? = null
    var english_translation: String? = null
    override fun toString(): String {
        return "SurahModel{" +
                "position=" + position +
                ", arabicText='" + arabicText + '\'' +
                ", englishText='" + englishText + '\'' +
                '}'
    }
}