package com.taibahai.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.utils.AppClass
import com.taibahai.R
import com.taibahai.activities.HistoryActivity
import com.taibahai.adapters.AdapterAISearch
import com.taibahai.adapters.AdapterChatPopups
import com.taibahai.databinding.FragmentSearchBinding
import com.taibahai.models.ModelChatPopups
import com.taibahai.models.ModelSearchAI
import com.taibahai.room_database.ChatDatabase
import com.taibahai.room_database.ChatMessageDao
import com.taibahai.room_database.ModelChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.UUID


class SearchFragment : BaseFragment(), OnItemClick {
    lateinit var binding: FragmentSearchBinding
    private val client = OkHttpClient()
    private lateinit var chatDatabase: ChatDatabase
    private lateinit var chatMessageDao: ChatMessageDao
    val showMessagePopups = ArrayList<ModelChatPopups>()
    val showMessage = ArrayList<ModelSearchAI>()
    lateinit var adapterMessagePopups: AdapterChatPopups
    private var userQuestion: String = ""
    private var botResponse: String? = null
    private var currentChatId: String? = null
    private val newMessages = mutableListOf<ModelChatMessage>()
    private var isNewMessage = false
    private val SPEECH_REQUEST_CODE = 123
    private var textToSpeech: TextToSpeech? = null
    private var spokenText: String? = null
    private var archiveMessageId: Long? = null
    private var isArchived: Boolean = false
    private var isAudioPlaying: Boolean = false
    private lateinit var messageAdapter: AdapterAISearch
    private lateinit var bottomNavigationView: BottomNavigationView
    private var isKeyboardOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentSearchBinding>(
            inflater, R.layout.fragment_search, container, false
        )
        bottomNavigationView = activity?.findViewById(R.id.bottomNavigationView) ?: return binding.root

        // Add a global layout listener to monitor layout changes, including keyboard visibility changes
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            // Check if the keyboard is open or closed
            val screenHeight = binding.getRoot().rootView.height
            val heightDiff = screenHeight - binding.getRoot().height
            val keyboardOpenThreshold = screenHeight / 3

            if (heightDiff > keyboardOpenThreshold) {
                // Keyboard is open
                if (!isKeyboardOpen) {
                    isKeyboardOpen = true
                    hideBottomNavigation()
                }
            } else {
                // Keyboard is closed
                if (isKeyboardOpen) {
                    isKeyboardOpen = false
                    showBottomNavigation()
                }
            }
        }
        return binding.root
    }


    private fun hideBottomNavigation() {
        bottomNavigationView.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {

                val availableVoices = textToSpeech!!.voices
                for (voice in availableVoices) {
                    Log.d("YourFragment", "Available Voice: ${voice.name}, Locale: ${voice.locale}")
                }
//                val locale = Locale("ar", "SA") // Arabic, Saudi Arabia
                val locale = Locale("en", "US") // Arabic, Saudi Arabia
                textToSpeech!!.language = locale

                textToSpeech!!.setPitch(1.0f) // Adjust pitch if needed
                textToSpeech!!.setSpeechRate(1.0f) // Adjust speech rate if needed
            } else {
                Log.e("YourFragment", "Text-to-Speech initialization failed")
            }
        })
    }


    override fun viewCreated() {
        isArchived = AppClass.sharedPref.getIsArchived() ?: false
        chatDatabase = ChatDatabase.getDatabase(requireContext())
        chatMessageDao = chatDatabase.chatMessageDao()
        messageAdapter = AdapterAISearch(requireContext(), showMessage, this)

        adapterMessagePopups = AdapterChatPopups(showMessagePopups) { message ->
            binding.messageBox.setText(message)
        }

        showTopMessagePopups()
        getAllMessages()

        if (isArchived) {
            archiveChat()
        } else {
            unarchiveChat()
        }
    }

    override fun clicks() {
        binding.sendBtn.setOnClickListener {
            userQuestion = binding.messageBox.text.toString().trim().toString()
            if (userQuestion != null) {
                if (userQuestion.isNotEmpty()) {
                    binding.messageBox.text.clear()

                    // Generate or use the existing conversationId for this conversation
                    currentChatId = UUID.randomUUID().toString()

                    // Save the user's message to the Room database
                    val userMessage = ModelChatMessage(
                        message = userQuestion, isUser = true, conversationId = currentChatId!!
                    )

                    GlobalScope.launch {
                        chatDatabase.chatMessageDao().insertMessage(userMessage)

                        // Send the user question to chatbot API
                        if (!spokenText.isNullOrEmpty()) {
                            askChatbot(spokenText!!)
                            spokenText = null  // Reset spokenText after sending the message
                        } else {
                            askChatbot(userQuestion)
                        }
                    }
                }
            }


        }

        binding.voiceBtn.setOnClickListener {
            startSpeechToText()
        }

        binding.ivDotsSelect.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }


    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something") // Change the language if needed


        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Speech recognition not available", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == -1 && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty()) {
                spokenText = results[0]
                binding?.messageBox?.setText(spokenText)  // Set spoken text in the message box
            }
        }
    }


    private fun updateUI(messages: List<ModelChatMessage>) {
        (binding.rvSearchAI.adapter as AdapterAISearch).messageList.clear()

        if (isNewMessage) {
            for (i in messages.indexOfFirst { it.id == archiveMessageId } + 1 until messages.size) {
                val message = messages[i]
                val modelMessage = ModelSearchAI(message.message, message.isUser)
                (binding.rvSearchAI.adapter as AdapterAISearch).messageList.add(modelMessage)
            }
        } else {
            // Display the entire chat
            for (message in messages) {
                val modelMessage = ModelSearchAI(message.message, message.isUser)
                (binding.rvSearchAI.adapter as AdapterAISearch).messageList.add(modelMessage)
            }
        }

        binding.rvSearchAI.adapter?.notifyDataSetChanged()

        val itemCount = (binding.rvSearchAI.adapter as AdapterAISearch).itemCount
        if (itemCount > 0) {
            binding.rvSearchAI.smoothScrollToPosition(itemCount - 1)
        }
    }


    private suspend fun getLastMessageId(): Long? = withContext(Dispatchers.IO) {
        val lastUserMessage = chatDatabase.chatMessageDao().getLastMessage()
        lastUserMessage?.id
    }


    private fun getAllMessages() {
        chatMessageDao.getAllMessages().observe(viewLifecycleOwner, Observer { messages ->
            updateUI(messages)
        })
    }


    private fun askChatbot(userQuestion: String) {
        getBotAnswer(userQuestion) { response ->
            activity?.runOnUiThread {
                botResponse = response
                displayBotResponse()

            }
        }
    }

    private fun getBotAnswer(question: String, callback: (String?) -> Unit) {


        val apiKey = "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"
        val url = "https://api.openai.com/v1/completions"
        val requestBody = """
    {
       "model": "gpt-3.5-turbo-instruct",
        "prompt": "In the context of Islam, $question",
        "max_tokens": 4000,
        "temperature": 0
    }
    """.trimIndent()


        val request = Request.Builder().url(url).header("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull())).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)

                    if (jsonObject.has("error")) {
                        val errorMessage =
                            jsonObject.getJSONObject("error").optString("message", "")
                        callback(errorMessage)
                        return
                    }

                    // Check if there is an array named "choices" in the response
                    if (jsonObject.has("choices")) {
                        val jsonArray: JSONArray = jsonObject.getJSONArray("choices")

                        if (jsonArray.length() > 0) {
                            val textResult = jsonArray.getJSONObject(0).optString("text", "")

                            if (textResult.isNotEmpty()) {
                                callback(textResult)
                                return
                            }
                        }
                    }

                    callback(null)

                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(null)
                }
            }
        })
    }


    private fun displayBotResponse() {
        // Save the bot response
        botResponse?.let {
            val botMessage =
                ModelChatMessage(message = it, isUser = false, conversationId = currentChatId ?: "")
            GlobalScope.launch {
                chatMessageDao.insertMessage(botMessage)
            }
            botResponse = null

        }
    }


    override fun initAdapter() {
        messageAdapter = AdapterAISearch(requireContext(), showMessage, object : OnItemClick {

            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                if (position >= 0 && position < showMessage.size) {
                    val textToSpeak = showMessage[position].message

                    when (type) {
                        "play" -> {
                            speakText(textToSpeak, position)
                            isAudioPlaying = true
                            updateVisibility(position)

                        }


                        "pause" -> {
                            textToSpeech?.stop()
                            isAudioPlaying = false
                            updateVisibility(position)
                        }


                        else -> {}
                    }
                } else {
                    Log.e("AISearch", "Invalid position: $position")
                }
            }


        })
        binding.rvSearchAI.adapter = messageAdapter
    }

    private var handler = Handler(Looper.getMainLooper())

    private fun speakText(text: String, position: Int) {
        textToSpeech?.let { tts ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueUtteranceId")

                tts.setOnUtteranceCompletedListener { utteranceId ->
                    if (utteranceId == "UniqueUtteranceId") {
                        isAudioPlaying = false
                        // Delay the visibility update for 500 milliseconds
                        handler.postDelayed({
                            updateVisibility(position)
                        }, 200)
                    }
                }

                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "UniqueUtteranceId")
            } else {
                @Suppress("deprecation") tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun updateVisibility(position: Int) {
        val itemView = binding.rvSearchAI.findViewHolderForAdapterPosition(position)?.itemView

        if (itemView != null) {
            val ivPlay = itemView.findViewById<ImageView>(R.id.ivPlay)
            val ivPause = itemView.findViewById<ImageView>(R.id.ivPause)

            if (!isAudioPlaying) {
                ivPlay.visibility = View.VISIBLE
                ivPause.visibility = View.INVISIBLE
            } else {
                ivPlay.visibility = View.INVISIBLE
                ivPause.visibility = View.VISIBLE
            }
        }
    }


    override fun onStop() {
        super.onStop()
        textToSpeech?.stop()
        isAudioPlaying = false

    }



    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        val archiveMenuItem = popupMenu.menu.findItem(R.id.menu_archive)

        archiveMenuItem.title = if (isArchived) "Unarchive" else "Archive"

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_history -> {
                    val intent = Intent(requireContext(), HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_archive -> {
                    if (isArchived) {
                        unarchiveChat()
                    } else {
                        archiveChat()
                    }
                    archiveMenuItem.title = if (isArchived) "Unarchive" else "Archive"
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    private fun archiveChat() {
        isArchived = true
        AppClass.sharedPref.setIsArchived(isArchived)

        // Save the Id of the last message before archiving
        GlobalScope.launch {
            archiveMessageId = getLastMessageId()
        }
        //  indicate new message
        isNewMessage = true
        // display new list after archiving
        updateUI(newMessages)
    }

    private fun unarchiveChat() {
        isArchived = false
        AppClass.sharedPref.setIsArchived(isArchived)
        archiveMessageId = null
        isNewMessage = false
        getAllMessages()
    }

    private fun showTopMessagePopups() {
        showMessagePopups.clear()
        showMessagePopups.add(ModelChatPopups("Merry Christmas"))
        showMessagePopups.add(ModelChatPopups("Happy Birthday"))
        showMessagePopups.add(ModelChatPopups("English Teacher"))
        showMessagePopups.add(ModelChatPopups("Cat-Friend"))
        showMessagePopups.add(ModelChatPopups("Happy Valentine's day"))
        showMessagePopups.add(ModelChatPopups("Horoscope"))
        showMessagePopups.add(ModelChatPopups("Quick Meal"))
        showMessagePopups.add(ModelChatPopups("Plan my Vacation"))
        showMessagePopups.add(ModelChatPopups("Business Idea"))
        showMessagePopups.add(ModelChatPopups("Tell Me a Joke"))

        adapterMessagePopups.setData(showMessagePopups)
        binding.rvTopMessagePopups.adapter = adapterMessagePopups

    }


}