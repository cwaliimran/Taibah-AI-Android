package com.taibahai.room_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages")
    fun getAllMessages(): LiveData<List<ModelChatMessage>>

    @Insert
    fun insertMessage(message: ModelChatMessage)

    @Query("DELETE FROM chat_messages")
    fun deleteAllMessages()

    @Query("SELECT * FROM chat_messages ORDER BY id DESC LIMIT 1")
    fun getLastMessage(): ModelChatMessage?


}