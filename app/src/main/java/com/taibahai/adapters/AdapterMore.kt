package com.taibahai.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.taibahai.R
import com.taibahai.activities.ImamsOfSunnaActivity
import com.taibahai.activities.BooksAndPDFActivity
import com.taibahai.activities.InheritanceLawActivity
import com.taibahai.activities.QuranChaptersActivity
import com.taibahai.activities.UpgradeActivity
import com.taibahai.activities.ZakatCalculatorActivity
import com.taibahai.databinding.ItemMoreBinding
import com.taibahai.fragments.SearchFragment
import com.taibahai.hadiths.HadithBooksActivity1
import com.taibahai.models.ModelMore
import com.taibahai.models.ModelMoreLevels
import com.taibahai.search_database_tablayout.SearchDatabaseActivity

class AdapterMore(private val context: Context, var showData: MutableList<ModelMore>) :
    RecyclerView.Adapter<AdapterMore.ViewHolder>() {
    lateinit var binding: ItemMoreBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMore.ViewHolder {
        binding = ItemMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMore.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelMore>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterMore.ViewHolder, position: Int) {
        val moreData = showData[position]
        holder.binding.model = moreData
        holder.binding.tvLevel.text = showData[position].level
        holder.binding.tvPackege.text = showData[position].packageName
        val adapter = AdapterMoreLevels(moreData.levelsList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                navigateToActivity(moreData.levelsList[position])
            }
        })
        holder.rvMoreLevelsList.adapter = adapter
        if (position == 0) {
            holder.binding.tvFree.visibility = View.VISIBLE
            holder.binding.tvLevel.visibility = View.INVISIBLE
            holder.binding.tvPackege.visibility = View.INVISIBLE
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
            val layoutParams = holder.rvMoreLevelsList.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = -40
            holder.rvMoreLevelsList.layoutParams = layoutParams
        } else {
            holder.binding.tvFree.visibility = View.INVISIBLE
            holder.binding.tvLevel.visibility = View.VISIBLE
            holder.binding.tvPackege.visibility = View.VISIBLE
            holder.binding.btnUpgrade.visibility = View.VISIBLE

        }

        binding.btnUpgrade.setOnClickListener {
            context.startActivity(Intent(context, UpgradeActivity::class.java))
        }

    }

    private fun navigateToActivity(model: ModelMoreLevels) {
        when (model.key) {

            "quran" -> {
                val intent = Intent(context, QuranChaptersActivity::class.java)
                context.startActivity(intent)
            }

            "hadith" -> {

                val intent = Intent(context, HadithBooksActivity1::class.java)
                context.startActivity(intent)
            }


            "zakat_calculator" -> {
                val intent = Intent(context, ZakatCalculatorActivity::class.java)
                context.startActivity(intent)
            }

            "imams" -> {
                val intent = Intent(context, ImamsOfSunnaActivity::class.java)
                context.startActivity(intent)
            }

            "books_pdfs" -> {
                val intent = Intent(context, BooksAndPDFActivity::class.java)
                context.startActivity(intent)
            }

            "inheritance_law" -> {
                val intent = Intent(context, InheritanceLawActivity::class.java)
                context.startActivity(intent)
            }

            "searchdb" -> {
                val intent = Intent(context, SearchDatabaseActivity::class.java)
                context.startActivity(intent)
            }


            else -> {

            }
        }
    }


    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvMoreLevelsList: RecyclerView = itemView.findViewById(R.id.rvMoreLevelsList)

    }
}