package com.network.models

import java.io.Serializable

data class ModelChapter(
    var id: String = "",
    var surah_number: String = "",
    var verse_number: String = "",
    var text: String = "",
    var translation_en: String = "",
    var transliteration_en: String = "",
) : Serializable