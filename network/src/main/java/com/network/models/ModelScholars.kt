package com.network.models

import java.io.Serializable

data class ModelScholars(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
):Serializable {
    data class Data(
        var id: String = "",
        var name: String = "",
        var era: String = "",
        var description: String = "",
        
        var updated_at: Any = Any(),
        var attachments: List<Attachment> = listOf(),
        var books: List<Book> = listOf()
    ):Serializable {
        data class Attachment(
            var `file`: String = "",
            var object_type: String = "",
            var file_type: String = ""
        ):Serializable

        data class Book(
            var book_id: String = "",
            var book_title: String = "",
            var book_file: String = ""
        ):Serializable
    }
}