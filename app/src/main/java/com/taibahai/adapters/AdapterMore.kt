package com.taibahai.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.activities.BookPDFDetailActivity
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
import com.taibahai.watch_and_learn.WatchAndLearnListActivity

class AdapterMore(private val context: Activity, var showData: MutableList<ModelMore>) :
    RecyclerView.Adapter<AdapterMore.ViewHolder>() {
    lateinit var binding: ItemMoreBinding

    //    var isSilverPurchased =
//        AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED)
//    var isGoldPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED)
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
        val holderPos = holder.absoluteAdapterPosition
        val moreData = showData[holderPos]
        holder.binding.model = moreData
        holder.binding.tvLevel.text = showData[holderPos].level
        holder.binding.tvPackege.text = showData[holderPos].packageName
        val adapter = AdapterMoreLevels(moreData.levelsList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                navigateToActivity(moreData.levelsList[position])
            }
        })
        holder.rvMoreLevelsList.adapter = adapter
        if (holderPos == 0) {
            holder.binding.tvFree.visibility = View.VISIBLE
            holder.binding.tvLevel.visibility = View.INVISIBLE
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
            val layoutParams = holder.rvMoreLevelsList.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = -40
            holder.rvMoreLevelsList.layoutParams = layoutParams
        } else {
            holder.binding.tvFree.visibility = View.INVISIBLE
            holder.binding.tvLevel.visibility = View.VISIBLE
//            holder.binding.btnUpgrade.visibility = View.VISIBLE

        }
//
//        if (position == 1 && isSilverPurchased) {
//            holder.binding.btnUpgrade.visibility = View.INVISIBLE
//        }
//        if (position == 2 && isGoldPurchased) {
//            holder.binding.btnUpgrade.visibility = View.INVISIBLE
//        }

        if (isDiamondPurchased) {
            holder.binding.btnUpgrade.text = "Subscribed"
        } else {
            holder.binding.btnUpgrade.text = "Upgrade"
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

            "english_translation" -> {
                val intent = Intent(context, BookPDFDetailActivity::class.java)
                intent.putExtra("title", "The Clear Quran English Translation")
                intent.putExtra(
                    "url",
                    "https://admin.taibahislamic.com/uploads/bdbf34d6bffe4ceddc5881e64260bfe0.pdf"
                )
                context.startActivity(intent)
            }

            "hadith" -> {

                val intent = Intent(context, HadithBooksActivity1::class.java)
                context.startActivity(intent)
            }


            "zakat_calculator" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, ZakatCalculatorActivity::class.java)
                    context.startActivity(intent)
                } else {
                    gotoUpgradeScreen(context)
                }
            }

            "imams" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, ImamsOfSunnaActivity::class.java)
                    context.startActivity(intent)
                } else {
                    gotoUpgradeScreen(context)
                }
            }

            "books_pdfs" -> {

                if (isDiamondPurchased) {
                    val intent = Intent(context, BooksCategoriesActivity::class.java)
                    context.startActivity(intent)
                } else
                    gotoUpgradeScreen(context)
            }

            "inheritance_law" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, InheritanceLawActivity::class.java)
                    context.startActivity(intent)
                } else
                    gotoUpgradeScreen(context)
            }

            "islamic_content_videos" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, WatchAndLearnListActivity::class.java)
                    context.startActivity(intent)
                } else
                    gotoUpgradeScreen(context)
            }

            "searchdb" -> {
                if (isDiamondPurchased) {
                    val intent = Intent(context, SearchDatabaseActivity::class.java)
                    context.startActivity(intent)
                } else
                    gotoUpgradeScreen(context)
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

    fun gotoUpgradeScreen(context: Context) {
        context.startActivity(Intent(context, UpgradeActivity::class.java))

    }
}