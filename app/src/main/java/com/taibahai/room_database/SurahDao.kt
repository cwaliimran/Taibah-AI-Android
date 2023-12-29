package com.taibahai.room_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SurahDao {
    @Insert
    fun insertFav(model: FavModel?)

    @Query("DELETE FROM favourite_table WHERE fav_position = :position")
    fun deleteFav(position: Long)

    @Query("UPDATE favourite_table SET is_download = :val WHERE fav_position = :position")
    fun setDownload(`val`: Boolean, position: String)

    @Query("SELECT * FROM favourite_table")
    fun getAllfav(): List<FavModel?>?
}