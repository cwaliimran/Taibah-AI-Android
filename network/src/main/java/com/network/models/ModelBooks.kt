package com.network.models

data class ModelBooks(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var title: String = "",
        var date: String = "",
        var created_at: String = "",
        var attachments: List<Attachment> = listOf()
    ) {
        data class Attachment(
            var `file`: String = "",
            var object_type: String = "",
            var file_type: String = ""
        )
    }
}