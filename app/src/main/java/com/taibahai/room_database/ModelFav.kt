package com.taibahai.room_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_table")
data class FavModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "fav_position")
    var position: Long,

    @ColumnInfo(name = "is_download")
    var isDownload: Boolean = false
) {
    // Secondary constructor if needed
    constructor(position: Long) : this(0, position, false)
}