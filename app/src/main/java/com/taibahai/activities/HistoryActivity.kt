package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterAISearch
import com.taibahai.databinding.ActivityHistoryBinding
import com.taibahai.models.ModelSearchAI
import com.taibahai.room_database.ChatDatabase
import com.taibahai.room_database.ChatMessageDao
import com.taibahai.room_database.ModelChatMessage

class HistoryActivity : BaseActivity() {
    lateinit var binding:ActivityHistoryBinding
    private lateinit var chatDatabase: ChatDatabase
    private lateinit var chatMessageDao: ChatMessageDao

    override fun onCreate() {
        binding=ActivityHistoryBinding.inflate(layoutInflater)
        chatDatabase = ChatDatabase.getDatabase(this)
        chatMessageDao = chatDatabase.chatMessageDao()
        setContentView(binding.root)
        getAllMessages()
    }

    override fun clicks() {

    }

    private fun getAllMessages() {
        chatMessageDao.getAllMessages().observe(this, Observer { messages ->
            updateUI(messages)
        })
    }

    override fun initAdapter() {
        super.initAdapter()
        val messageAdapter = AdapterAISearch(this, ArrayList())
        binding?.rvHistory?.adapter = messageAdapter
    }


    private fun updateUI(messages: List<ModelChatMessage>) {
        (binding?.rvHistory?.adapter as AdapterAISearch).messageList.clear()

        for (message in messages) {
            val modelMessage = ModelSearchAI(message.message, message.isUser)
            //val chatMessage = ChatMessage(modelMessage, message.conversationId)
            (binding?.rvHistory?.adapter as AdapterAISearch).messageList.add(modelMessage)
        }

        binding?.rvHistory?.adapter?.notifyDataSetChanged()
    }
}