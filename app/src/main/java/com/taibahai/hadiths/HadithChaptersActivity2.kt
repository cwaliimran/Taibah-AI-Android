package com.taibahai.hadiths

import android.content.Intent
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelHadithChapter2
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.adapters.AdapterHadithChapter
import com.taibahai.databinding.ActivityHadithChapters2Binding
import com.taibahai.utils.showToast

class HadithChaptersActivity2 : BaseActivity() {
    lateinit var binding: ActivityHadithChapters2Binding
    val showList = ArrayList<ModelHadithChapter2.Data>()
    lateinit var adapter: AdapterHadithChapter
    var book_id = ""
    var imam_name = ""
    var total_chapter = ""
    var title = ""
    private val viewModel: MainViewModelTaibahIslamic by viewModels()


    override fun onCreate() {
        binding = ActivityHadithChapters2Binding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterHadithChapter(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                super.onClick(position, type, data)


                val intent = Intent(context, ChapterHadithsActivity3::class.java)
                intent.putExtra("book_id", showList[position].id)
                intent.putExtra("chapter_name", showList[position].chapter_name)
                intent.putExtra("hadith_number", showList[position].hadith_number)
                intent.putExtra("title", title)
                context.startActivity(intent)
            }
        })

        binding.rvHadithChapter.adapter = adapter

    }

    override fun initObservers() {
        super.initObservers()
        viewModel.hadithChapterLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    it.data?.data?.let { it1 -> showList.addAll(it1) }
                    binding.tvHadithChapter.text = title

                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            book_id = intent.getStringExtra("book_id").toString()
            imam_name = intent.getStringExtra("imam_name").toString()
            total_chapter = intent.getStringExtra("total_chapter").toString()
            title = intent.getStringExtra("title").toString()


        }

        viewModel.getHadithChapters(1, book_id, "yes")

    }
}