package com.taibahai.quran

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity annotation for giving table name and declaring it
@Entity(tableName = "favourite_table")
class FavModel(@field:ColumnInfo(name = "fav_position") var position: Long) {
    //@primary Key to set id as primary key
    // and making auto increment for each new list
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "is_download")
    var isDownload = false

}