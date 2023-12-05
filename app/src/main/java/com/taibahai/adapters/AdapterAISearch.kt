package com.taibahai.adapters

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.taibahai.R
import com.taibahai.models.ModelSearchAI

class AdapterAISearch( val context: Context, val messageList: ArrayList<ModelSearchAI>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ItemSend = 1
    val ItemReceive = 2
    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
            }
        })
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ItemSend) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_send_message, parent, false)
            return SentMessageViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_receive_message, parent, false)
            return ReceiveMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        val textToSpeak = currentMessage.message
        if (holder.itemViewType == ItemSend) {
            val viewHolder = holder as SentMessageViewHolder
            viewHolder.sendMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveMessageViewHolder
            viewHolder.receiveMessage.text = currentMessage.message

            holder.ivSpeak.setOnClickListener {

                speakText(textToSpeak)


            }
        }
    }

    private fun speakText(text: String) {
        textToSpeech?.let { tts ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueUtteranceId")
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "UniqueUtteranceId")
            } else {
                @Suppress("deprecation")
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    // Method to stop TextToSpeech
    fun stopTextToSpeech() {
        textToSpeech?.stop()
    }



    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.isUser) {
            ItemSend
        } else {
            ItemReceive
        }
    }




    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sendMessage = itemView.findViewById<TextView>(R.id.tvSendMessage)
    }

    class ReceiveMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.tvResponse)
        val ivSpeak=itemView.findViewById<ImageView>(R.id.ivSpeak)

    }
}