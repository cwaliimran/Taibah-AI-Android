package com.taibahai.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ModelChatMessage::class],
    version = 1,
    exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {

            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ChatDatabase::class.java,
                        "appDatabase"
                    ).build()

                }
            }
            return INSTANCE!!
        }
    }
}