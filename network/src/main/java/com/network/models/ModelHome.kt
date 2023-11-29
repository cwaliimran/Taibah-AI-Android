package com.network.models

data class ModelHome(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf(),
    var meta: Meta = Meta()
) {
    data class Data(
        var user: User = User(),
        var relation_details: RelationDetails = RelationDetails()
    ) {
        data class User(
            var id: String = "",
            var name: String = "",
            var age: String = "",
            var country: Country = Country(),
            var gender: String = "",
            var selfie: String = "",
            var attachments: List<Attachment> = listOf()
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
        }

        data class RelationDetails(
            var matched: Boolean = false,
            var liked: Boolean = false,
            var visited: Boolean = false
        )
    }

    data class Meta(
        var current_page: Int = 0,
        var last_page: Int = 0,
        var per_page: String = "",
        var total: Int = 0
    )
}