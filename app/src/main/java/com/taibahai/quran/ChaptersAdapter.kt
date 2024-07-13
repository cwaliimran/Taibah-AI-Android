package com.taibahai.quran

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelChapter
import com.network.utils.AppClass
import com.taibahai.R
import com.taibahai.databinding.ItemQuranChapterDetailBinding
import com.taibahai.utils.ShareImage.getBitmapFromView

class ChaptersAdapter(
    var mData: MutableList<ModelChapter>,
    private val context: Context,
    var showFooter: Boolean? = true,
) :
    RecyclerView.Adapter<ChaptersAdapter.HomeListHolder>() {
    private var listener: AdapterView.OnItemClickListener? = null
    var layoutInflater: LayoutInflater

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun updateList(tasks: MutableList<ModelChapter>) {
        var oldSize = mData.size
        mData = tasks
        notifyItemRangeInserted(oldSize, mData.size)
    }

    fun clearList() {
        mData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quran_chapter_detail, parent, false)
        return HomeListHolder(ItemQuranChapterDetailBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: HomeListHolder, position: Int) {
        val model = mData[position]
        if (showFooter == true) {
            if (position == mData.size - 1) {
                holder.binding.footer.visibility = View.VISIBLE
            } else {
                holder.binding.footer.visibility = View.GONE
            }
        }
        holder.binding.ayatArabicText.text = model.text
       holder.binding.surahName.text= model.surah_name
        holder.binding.ayatEnglishTranslation.text = model.translation_en
        holder.binding.ayatEnglishTranslitration.text =
            Html.fromHtml(model.transliteration_en, Html.FROM_HTML_MODE_LEGACY)
        holder.binding.ayatNumber.text = model.verse_number.toString()


        holder.binding.ivShare.setOnClickListener {
            context.getBitmapFromView(holder.binding.root)

        }
        val textSize = AppClass.sharedPref.getInt(StringUtils.FONT_SIZE)
        if (textSize != 0) {
            holder.binding.ayatArabicText.textSize = textSize.toFloat()
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class HomeListHolder(var binding: ItemQuranChapterDetailBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}