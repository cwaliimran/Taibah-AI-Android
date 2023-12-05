package com.network.models

data class ModelUploadFile(
    var status: Int = 0,
    var message: String = "",
    var `data`: Data = Data()
) {
    data class Data(
        var `file`: String = "",
        var url: String = ""
    )
}