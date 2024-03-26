package com.taibahai.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelSurahDetail
import com.taibahai.databinding.ItemQuranChapterDetailBinding

class AdapterQuranDetail(var showData: List<ModelSurahDetail>):RecyclerView.Adapter<AdapterQuranDetail.ViewHolder>() {
    lateinit var binding: ItemQuranChapterDetailBinding

    override fun onCreateViewHolder( parent: ViewGroup,viewType: Int): AdapterQuranDetail.ViewHolder {
        binding = ItemQuranChapterDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQuranDetail.ViewHolder(binding)
    }
    fun setData(list: List<ModelSurahDetail>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterQuranDetail.ViewHolder, position: Int) {
        val chapterData = showData[position]
        holder.binding.model = chapterData
        holder.binding.ayatNumber.text= showData[position].position
        holder.binding.ayatArabicText.text=showData[position].arabic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.ayatEnglishTranslitration.setText(
                Html.fromHtml(
                    chapterData.english_transliteration,
                    Html.FROM_HTML_MODE_LEGACY
                )
            )
        } else {
            holder.binding.ayatEnglishTranslitration.setText(Html.fromHtml(chapterData.english_transliteration))
        }
        holder.binding.ayatEnglishTranslation.text=showData[position].english_translation

    }

    override fun getItemCount(): Int {
        return showData.size

    }
    class ViewHolder(val binding: ItemQuranChapterDetailBinding) : RecyclerView.ViewHolder(binding.root) {}
}