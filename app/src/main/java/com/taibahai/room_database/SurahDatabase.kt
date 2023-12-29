package com.taibahai.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavModel::class],
    version = 1,
    exportSchema = false)
abstract class SurahDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao

    companion object {
        @Volatile
        private var INSTANCE: SurahDatabase? = null

        fun getDatabase(context: Context): SurahDatabase {

            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        SurahDatabase::class.java,
                        "SurahDatabase"
                    ).build()

                }
            }
            return INSTANCE!!
        }
    }
}