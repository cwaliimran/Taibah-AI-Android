package com.network.models

data class ModelScholars(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var name: String = "",
        var era: String = "",
        var description: String = "",
        var created_at: String = "",
        var updated_at: Any = Any(),
        var attachments: List<Attachment> = listOf(),
        var books: List<Any> = listOf()
    ) {
        data class Attachment(
            var `file`: String = "",
            var object_type: String = "",
            var file_type: String = ""
        )
    }
}