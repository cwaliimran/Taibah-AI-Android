package com.taibahai.activities


import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterUpgrade
import com.taibahai.databinding.ActivityUpgradeBinding
import com.taibahai.models.ModelUpgrade
import com.taibahai.models.ModelUpgradeList

class UpgradeActivity : BaseActivity() {
    lateinit var binding:ActivityUpgradeBinding
    lateinit var adapter: AdapterUpgrade
    val showList = ArrayList<ModelUpgrade>()



    override fun onCreate() {
        binding= ActivityUpgradeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addViewPager()
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()

        val upgradeBasic=ArrayList<ModelUpgradeList>()
        upgradeBasic.add(ModelUpgradeList("300 AI Tokens/Month"))
        upgradeBasic.add(ModelUpgradeList("Ads"))
        upgradeBasic.add(ModelUpgradeList("Quran"))
        upgradeBasic.add(ModelUpgradeList("Hadith"))
        upgradeBasic.add(ModelUpgradeList("Zakat Calculator"))

        showList.add(ModelUpgrade("1","Basic","Silver Package",upgradeBasic,"\$2.99/month"))


        val upgradeAdvance=ArrayList<ModelUpgradeList>()
        upgradeAdvance.add(ModelUpgradeList("700 AI Tokens/Month"))
        upgradeAdvance.add(ModelUpgradeList("No Ads"))
        upgradeAdvance.add(ModelUpgradeList("Quran"))
        upgradeAdvance.add(ModelUpgradeList("Hadith"))
        upgradeAdvance.add(ModelUpgradeList("Zakat Calculator"))
        upgradeAdvance.add(ModelUpgradeList("4 Influential Scholars"))

        showList.add(ModelUpgrade("2","Advance","Gold Package",upgradeAdvance,"\$4.99/month"))


        val upgradeExclusive=ArrayList<ModelUpgradeList>()
        upgradeExclusive.add(ModelUpgradeList("Unlimited AI Tokens/M"))
        upgradeExclusive.add(ModelUpgradeList("No Ads"))
        upgradeExclusive.add(ModelUpgradeList("Quran"))
        upgradeExclusive.add(ModelUpgradeList("Hadith"))
        upgradeExclusive.add(ModelUpgradeList("4 Influential Scholars"))
        upgradeExclusive.add(ModelUpgradeList("Islamic Literature"))
        upgradeExclusive.add(ModelUpgradeList("Zakat Calculator"))
        upgradeExclusive.add(ModelUpgradeList("Inheritance Law"))
        upgradeExclusive.add(ModelUpgradeList("Search Database"))


        showList.add(ModelUpgrade("3","Exclusive","Diamond Package",upgradeExclusive,"\$9.99/month"))
    }


    private fun addViewPager()
    {
        binding.viewPager.adapter = AdapterUpgrade(showList)
        binding.viewPager.let { binding.dotsIndicator.attachTo(it) }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)






            }
        })

    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Upgrade")
        
        
    }

}