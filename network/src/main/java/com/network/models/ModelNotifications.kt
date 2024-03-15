package com.network.models

data class ModelNotifications(
    var `data`: List<Data> = listOf(),
    var meta: Meta = Meta()
) {
    data class Data(
        var title: String = "",
        var description: String = "",
        var created_at: String = "",
        var badge: String = ""
    )

    data class Meta(
        var current_page: Int = 0,
        var last_page: Int = 0,
        var path: String = "",
        var per_page: String = "",
        var total: Int = 0
    )
}