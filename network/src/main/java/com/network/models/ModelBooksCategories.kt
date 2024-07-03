package com.network.models

data class ModelBooksCategories(
    val status: Int,
    val message: String,
    val `data`: List<Data>
) {
    data class Data(
        val id: String,
        val title: String,
        val status: String = "",
        val created_at: String = "",
        val updated_at: String = "",
        val image: String = ""
    )
}