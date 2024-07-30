package com.network.models

import java.io.Serializable

data class ModelHome(
    var status: Int = 0,
    var message: String = "",
    var total_pages: Int = 0,
    var data: MutableList<Data> = mutableListOf()
) : Serializable {
    data class Data(
        var feed_id: String = "",
        var description:String="",
        var scientific_description: String = "",
        var user_name: String = "",
        var user_image: String = "",
        var timesince: String = "",
        var feed_attachments: List<FeedAttachment> = listOf(),
        var comments: Int = 0,
        var is_like: Boolean = false,
        var likes: Int = 0,
        var post_type:String=""
    ) : Serializable {
        data class FeedAttachment(
            var file: String = "",
            var file_type: String = ""
        ) : Serializable
    }
}