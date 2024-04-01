package com.network.models

data class ModelNotifications(
    var status: Int = 0,
    var message: String = "",
    var total_pages: Int = 0,
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var title: String = "",
        var message: String = "",
        var body: String = "",
        var created_at: String = ""
    )
}