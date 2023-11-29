package com.network.models

data class ModelProfile(
    var status: Int = 0,
    var message: String = "",
    var `data`: Data = Data()
) {
    data class Data(
        var id: String = "",
        var name: String = "",
        var age: String = "",
        var country: Country = Country(),
        var gender: String = "",
        var selfie: String = "",
        var attachments: List<Attachment> = listOf(),
        var relation_details: RelationDetails = RelationDetails()
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

        data class RelationDetails(
            var matched: Boolean = false,
            var liked: Boolean = false,
            var visited: Boolean = false
        )
    }
}