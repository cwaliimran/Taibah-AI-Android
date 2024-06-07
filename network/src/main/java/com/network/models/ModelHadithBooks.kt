package com.network.models

data class ModelHadithBooks(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var title: String = "",
        var imam: String = "",
        var is_active: String = "",
        var total_chapters: String = ""
    ) {
        override fun toString(): String {
            return "$id. $title"
        }
    }
}