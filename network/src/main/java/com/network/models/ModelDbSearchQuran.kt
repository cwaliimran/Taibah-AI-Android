package com.network.models

data class ModelDbSearchQuran(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var name: String = "",
        var transliteration_en: String = "",
        var translation_en: String = "",
        var total_verses: String = "",
        var revelation_type: String = ""
    )
}