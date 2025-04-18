package com.taibahai.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.adapters.AdapterAISearch
import com.taibahai.databinding.ActivityHistoryBinding
import com.taibahai.databinding.DialogHistoryBinding
import com.taibahai.models.ModelSearchAI
import com.taibahai.room_database.ChatDatabase
import com.taibahai.room_database.ChatMessageDao
import com.taibahai.room_database.ModelChatMessage
import com.taibahai.utils.AppTourDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryActivity : BaseActivity() {
    lateinit var binding: ActivityHistoryBinding
    private lateinit var chatDatabase: ChatDatabase
    private lateinit var chatMessageDao: ChatMessageDao
    private var appTourList = AppClass.sharedPref.getList<String>(AppConstants.APP_TOUR_TYPE)

    override fun onCreate() {
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        chatDatabase = ChatDatabase.getDatabase(this)
        chatMessageDao = chatDatabase.chatMessageDao()
        setContentView(binding.root)
        getAllMessages()
        if (!appTourList.contains("historyList")) {
            AppTourDialog.appTour(
                this,
                binding.ivDeleteHistory,
                "Delete History",
                "On the History Screen, you can manage your past interactions. Tapping the Delete Icon will prompt a confirmation popup, ensuring you want to permanently delete your history before proceeding."
            ) {
                appTourList.add("historyList")
                appTourList.add("historyDelete")
                AppClass.sharedPref.storeList(
                    AppConstants.APP_TOUR_TYPE,
                    appTourList
                )
                showDeleteDialog(true)

            }
        }
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivDeleteHistory.setOnClickListener {
            showDeleteDialog(false)
        }

    }

    private fun getAllMessages() {
        chatMessageDao.getAllMessages().observe(this, Observer { messages ->
            updateUI(messages)
        })
    }

    override fun initAdapter() {
        super.initAdapter()
        val messageAdapter = AdapterAISearch(this, ArrayList(), object : OnItemClick {

        })
        binding.rvHistory?.adapter = messageAdapter
    }


    private fun updateUI(messages: List<ModelChatMessage>) {
        (binding.rvHistory?.adapter as AdapterAISearch).messageList.clear()

        for (message in messages) {
            val modelMessage = ModelSearchAI(message.message, message.isUser)
            //val chatMessage = ChatMessage(modelMessage, message.conversationId)
            (binding.rvHistory?.adapter as AdapterAISearch).messageList.add(modelMessage)
        }

        binding.rvHistory?.adapter?.notifyDataSetChanged()
    }

    private fun showDeleteDialog(appTour: Boolean = false) {
        val dialog = Dialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val binding = DialogHistoryBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnYes.setOnClickListener {
            GlobalScope.launch {
                chatDatabase.chatMessageDao().deleteAllMessages()
            }
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

        if (appTour) {
            AppTourDialog.appTour(
                this,
                binding.btnYes,
                "Delete",
                "This button will delete your entire history. Please be cautious, as this action cannot be undone."
            ) {
                AppClass.sharedPref.storeList(
                    AppConstants.APP_TOUR_TYPE,
                    appTourList
                )
                finish()
            }
        }
    }
}