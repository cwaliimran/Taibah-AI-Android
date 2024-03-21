package com.network.models

import java.io.Serializable

data class ModelUpcoming(
    var status: Int = 0,
    var message: String = "",
    var `data`: List<Data> = listOf()
) {
    data class Data(
        var id: String = "",
        var title: String = "",
        var details: String = "",
        var icon: String = "",
        var created_at: String = ""
    ):Serializable
}