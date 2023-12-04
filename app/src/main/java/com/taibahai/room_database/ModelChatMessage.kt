package com.taibahai.room_database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taibahai.models.ModelSearchAI

@Entity(tableName = "chat_messages")
data class ModelChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val message: String,
    val conversationId: String, // Add conversationId to link user and bot messages
    val isUser: Boolean,


    ) {
    constructor(modelMessage: ModelSearchAI , conversationId: String) : this(
        message = modelMessage.message,
        isUser = modelMessage.isUser,
        conversationId = conversationId,


        )
}