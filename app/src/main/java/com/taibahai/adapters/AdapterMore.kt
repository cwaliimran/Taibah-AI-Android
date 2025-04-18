package com.taibahai.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.activities.BooksCategoriesActivity
import com.taibahai.activities.ImamsOfSunnaActivity
import com.taibahai.activities.InheritanceLawActivity
import com.taibahai.activities.UpgradeActivity
import com.taibahai.activities.ZakatCalculatorActivity
import com.taibahai.databinding.ItemMoreBinding
import com.taibahai.hadiths.HadithBooksActivity1
import com.taibahai.models.ModelMore
import com.taibahai.models.ModelMoreLevels
import com.taibahai.quran.QuranChaptersActivity
import com.taibahai.search_database_tablayout.SearchDatabaseActivity
import com.taibahai.utils.AppTourDialog

class AdapterMore(private val context: Activity, var showData: MutableList<ModelMore>) :
    RecyclerView.Adapter<AdapterMore.ViewHolder>() {
    lateinit var binding: ItemMoreBinding

    var isSilverPurchased =
        AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED)
    var isGoldPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED)
    var isDiamondPurchased =
        AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelMore>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moreData = showData[position]
        holder.binding.model = moreData
        holder.binding.tvLevel.text = showData[position].level
        holder.binding.tvPackege.text = showData[position].packageName
        val adapter = AdapterMoreLevels(moreData.levelsList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                navigateToActivity(moreData.levelsList[position])
            }
        })
        holder.rvMoreLevelsList.adapter = adapter
        if (position == 0) {
            holder.binding.tvFree.visibility = View.VISIBLE
            holder.binding.tvLevel.visibility = View.INVISIBLE
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
            val layoutParams = holder.rvMoreLevelsList.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = -40
            holder.rvMoreLevelsList.layoutParams = layoutParams
        } else {
            holder.binding.tvFree.visibility = View.INVISIBLE
            holder.binding.tvLevel.visibility = View.VISIBLE
            holder.binding.btnUpgrade.visibility = View.VISIBLE

        }

        if (position == 1 && isSilverPurchased) {
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
        }
        if (position == 2 && isGoldPurchased) {
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
        }

        if (isDiamondPurchased) {
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
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
                if (isGoldPurchased || isDiamondPurchased) {
                    val intent = Intent(context, ZakatCalculatorActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Please upgrade to Gold Package", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            "imams" -> {
                if (isGoldPurchased || isDiamondPurchased) {
                    val intent = Intent(context, ImamsOfSunnaActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Please upgrade to Gold Package", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            "books_pdfs" -> {

                if (isDiamondPurchased) {
                    val intent = Intent(context, BooksCategoriesActivity::class.java)
                    context.startActivity(intent)
                } else
                    Toast.makeText(context, "Please upgrade to Diamond Package", Toast.LENGTH_SHORT)
                        .show()
            }

            "inheritance_law" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, InheritanceLawActivity::class.java)
                    context.startActivity(intent)
                } else
                    Toast.makeText(context, "Please upgrade to Diamond Package", Toast.LENGTH_SHORT)
                        .show()
            }

            "searchdb" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, SearchDatabaseActivity::class.java)
                    context.startActivity(intent)
                } else
                    Toast.makeText(context, "Please upgrade to Diamond Package", Toast.LENGTH_SHORT)
                        .show()
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