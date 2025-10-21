package com.network.models

data class ModelGetInfluencers(
    var `data`: Data = Data(),
    var message: String = "",
    var status: Int = 0,
    var success: Boolean = false
) {
    data class Data(
        var influencers: List<Influencer> = listOf(),
        var meta: Meta = Meta()
    ) {
        data class Influencer(
            var created_at: String = "",
            var created_by: String = "",
            var created_by_name: Any = Any(),
            var description: String = "",
            var id: String = "",
            var image: String = "",
            var image_url: String = "",
            var name: String = "",
            var status: String = "",
            var total_podcasts: String = ""
        )

        data class Meta(
            var limit: Int = 0,
            var page: Int = 0,
            var sortField: String = "",
            var sortOrder: String = ""
        )
    }
}