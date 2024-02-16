package com.taibahai.hadiths

import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelChapterHadith3
import com.network.models.ModelDbSearchHadith
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.databinding.ActivityChapterHadiths3Binding
import com.taibahai.utils.showToast

class ChapterHadithsActivity3 : BaseActivity() {
    lateinit var binding: ActivityChapterHadiths3Binding
    val showList = ArrayList<ModelChapterHadith3.Data>()
    lateinit var adapter: AdapterChapterHadiths
    val viewModel: MainViewModelTaibahIslamic by viewModels()
    var chapter_id = ""
    var chapterName = ""
    var hadithNo = ""
    var title = ""
    var hadithType = ""
    private var currentPageNo = 1
    private var totalPages: Int = 1


    override fun onCreate() {
        binding = ActivityChapterHadiths3Binding.inflate(layoutInflater)

        setContentView(binding.root)
    }


    override fun clicks() {

        binding.rvChapterHadiths.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                if (dy > 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == showList.size) {
                        if (currentPageNo < totalPages) {
                            currentPageNo += 1
                            viewModel.getChapterHadiths(currentPageNo, chapter_id)
                        }
                    }

                }
            }
        })

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.chapterHadithLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                    totalPages = it.data?.total_pages!!
                    val oldSize = showList.size
                    it.data?.let { it1 -> showList.addAll(it1.data) }
                    binding.tvChapterHadith.text = chapterName
                    binding.tvChaptersFrom.text = hadithNo
                    if (oldSize == 0) {
                        initAdapter()
                    } else {
                        adapter.notifyItemRangeInserted(oldSize, showList.size)
                    }

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }


    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterChapterHadiths(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                super.onClick(position, type, data)
                val intent = Intent(context, HadithDetailsActivity4::class.java)
                intent.putExtra("id", showList[position].chapter_id)
                intent.putExtra("hadith_id", showList[position].hadith_no)
                intent.putExtra("hadith_number", hadithNo)
                intent.putExtra("chapter_name", chapterName)
                startActivity(intent)
            }

        })
        binding.rvChapterHadiths.adapter = adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            chapter_id = intent.getStringExtra("book_id").toString()
            chapterName = intent.getStringExtra("chapter_name").toString()
            hadithNo = intent.getStringExtra("hadith_number").toString()
            title = intent.getStringExtra("title").toString()
        }
        viewModel.getChapterHadiths(currentPageNo, chapter_id)
    }
}