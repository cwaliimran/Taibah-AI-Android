package com.network.models

import java.io.Serializable

data class ModelBooks(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) : Serializable {
    data class Data(
        var id: String = "",
        var title: String = "",
        var date: String = "",
        
        var attachments: List<Attachment> = listOf()
    ) : Serializable {
        data class Attachment(
            var `file`: String = "",
            var object_type: String = "",
            var file_type: String = ""
        ) : Serializable
    }
}