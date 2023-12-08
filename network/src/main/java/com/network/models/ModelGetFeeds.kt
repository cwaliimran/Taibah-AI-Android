package com.network.models

data class ModelGetFeeds(
    var status: Int = 0,
    var message: String = "",
    var `data`: Data = Data()
) {
    data class Data(
        var feed_id: String = "",
        var description: String = "",
        var user_name: String = "",
        var user_image: String = "",
        var created_at: String = "",
        var timesince: String = "",
        var feed_attachments: List<Any> = listOf(),
        var comments: List<ModelComments> = listOf()
    )
}