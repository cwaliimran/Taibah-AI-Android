package com.network.models

data class ModelPodcastsResponse(
    var `data`: Data = Data(),
    var message: String = "",
    var status: Int = 0,
    var success: Boolean = false
) {
    data class Data(
        var meta: Meta = Meta(),
        var podcasts: List<Podcast> = listOf()
    ) {
        data class Meta(
            var limit: Int = 0,
            var page: Int = 0,
            var sortField: String = "",
            var sortOrder: String = ""
        )

        data class Podcast(
            var created_at: String = "",
            var created_by: String = "",
            var description: String = "",
            var id: String = "",
            var influencer_id: String = "",
            var influencer_name: String = "",
            var media: String = "",
            var media_url: String = "",
            var status: String = "",
            var title: String = ""
        )
    }
}