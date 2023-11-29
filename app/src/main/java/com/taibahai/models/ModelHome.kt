package com.taibahai.models

import java.util.Date

data class ModelHome(
    val profileImage: Int,
    val userName: String,
    //val currentTime: Date? = Date(),
    val timesAgo :String,
    val userDescription: String,
    val userPost: Int,
    var likes: MutableList<String> = mutableListOf(),
)