package com.network.models

data class ModelUser(
    var status: Int = 0,
    var total_pages: Int = 0,
    var message: String = "",
    var data: Data = Data()
) {
    data class Data(
        var id: String = "",
        var name: String = "",
        var email: String = "",
        var image: String = "",
        var accesstoken: String = "",
        var social_id: Any = Any(),
        var social_type: String = "",
        
        var feed: MutableList<ModelHome.Data> = mutableListOf()
    )
}