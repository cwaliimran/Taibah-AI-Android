package com.network.models

data class ModelHome(
    var status: Int = 0,
    var message: String = "",
    var total_pages: Int = 0,
    var data: List<Data> = listOf()
) {
    data class Data(
        var feed_id: String = "",
        var description: String = "",
        var user_name: String = "",
        var user_image: String = "",
        var created_at: String = "",
        var timesince: String = "",
        var feed_attachments: List<FeedAttachment> = listOf(),
        var comments: Int = 0,
        var likes: Int = 0
    ) {
        data class FeedAttachment(
            var file: String = "",
            var file_type: String = ""
        )
    }
}