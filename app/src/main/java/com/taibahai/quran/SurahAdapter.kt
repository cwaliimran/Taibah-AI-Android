package com.taibahai.quran

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.utils.AppClass
import com.taibahai.R
import com.taibahai.databinding.ItemQuranChapterDetailBinding

class SurahAdapter(private val context: Context) :
    RecyclerView.Adapter<SurahAdapter.HomeListHolder>() {
    private var listener: OnItemClickListener? = null
    private var mData: List<SurahModel>?
    var layoutInflater: LayoutInflater

    init {
        mData = ArrayList()
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun updateList(tasks: List<SurahModel>?) {
        mData = tasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quran_chapter_detail, parent, false)
        return HomeListHolder(ItemQuranChapterDetailBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: HomeListHolder, position: Int) {
        val model = mData!![position]
        if (position == mData!!.size - 1) {
            holder.binding.footer.visibility = View.VISIBLE
        } else {
            holder.binding.footer.visibility = View.GONE
        }
        holder.binding.ayatArabicText.text = model.arabicText
        holder.binding.ayatEnglishTranslation.text = model.englishText
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.ayatEnglishTranslitration.text =
                Html.fromHtml(model.english_translation, Html.FROM_HTML_MODE_LEGACY)
        } else {
            holder.binding.ayatEnglishTranslitration.text = Html.fromHtml(model.english_translation)
        }
        holder.binding.ayatNumber.text = model.position.toString()


//        holder.binding.shareImage.setOnClickListener(v -> {
//            if (listener != null && position != RecyclerView.NO_POSITION) {
//                listener.onItemClick(holder.itemView, model);
//            }
//        });
        val textSize = AppClass.sharedPref.getInt(StringUtils.FONT_SIZE)
        if (textSize != 0) {
            holder.binding.ayatArabicText.textSize = textSize.toFloat()
        }
    }

    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, homeModel: SurahModel?)
    }

    fun setOnItemClickListner(listener: OnItemClickListener?) {
        this.listener = listener
    }

    class HomeListHolder(var binding: ItemQuranChapterDetailBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}