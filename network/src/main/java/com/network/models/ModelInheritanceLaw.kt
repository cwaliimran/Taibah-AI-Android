package com.network.models

data class ModelInheritanceLaw(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var title: String = "",
        var description: String = "",
        var type: String = "",
        var created_at: String = "",
        var updated_at: Any = Any(),
        var attachment: Attachment = Attachment()
    ) {
        data class Attachment(
            var `file`: String = ""
        )
    }
}