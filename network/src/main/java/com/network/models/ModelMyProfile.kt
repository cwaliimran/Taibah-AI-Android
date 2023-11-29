package com.network.models

data class ModelMyProfile(
    var status: Int = 0,
    var message: String = "",
    var new_user: Boolean = false,
    var `data`: Data = Data()
) {
    data class Data(
        var id: String = "",
        var email: String = "",
        var name: String = "",
        var age: String = "",
        var country: Country = Country(),
        var gender: String = "",
        var selfie: String = "",
        var attachments: List<Attachment> = listOf(),
        var profile_stats: ProfileStats = ProfileStats(),
        var likes: Likes = Likes(),
        var subscriptions: List<Subscription> = listOf(),
        var device_id: String = "",
        var device_type: String = "",
        var timezone: String = "",
        var created_at: String = "",
        var updated_at: String = ""
    ) {
        data class Country(
            var id: String = "",
            var name: String = "",
            var flag: String = ""
        )

        data class Attachment(
            var id: String = "",
            var name: String = "",
            var url: String = "",
            var type: String = ""
        )

        data class ProfileStats(
            var total_likes: String = "",
            var total_matches: String = ""
        )

        data class Likes(
            var total: Int = 0,
            var remaining: Int = 0,
            var limited: Boolean = false
        )

        data class Subscription(
            var plan_id: String = "",
            var is_expired: Boolean = false,
            var subscribed_on: String = ""
        )
    }
}