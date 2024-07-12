package com.taibahai.search_database_tablayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.models.ModelChapterHadith3
import com.network.models.ModelHadithBooks
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.databinding.FragmentTopHadithBinding
import com.taibahai.hadiths.HadithDetailsActivity4
import com.taibahai.utils.SpinnerAdapterHelper
import com.taibahai.utils.showToast


class TopHadithFragment : BaseFragment() {
    private lateinit var binding: FragmentTopHadithBinding

    lateinit var adapter: AdapterChapterHadiths
    val booksList = ArrayList<ModelHadithBooks.Data>()
    val showHadithData = ArrayList<ModelChapterHadith3.Data>()
    private val viewModel: MainViewModelTaibahIslamic by viewModels()
    val viewModelHadith: MainViewModelAI by viewModels()
    val handler = Handler(Looper.getMainLooper())
    var searchRunnable: Runnable? = null
    val hadithBooks = mutableListOf<ModelHadithBooks.Data>()
    var selectedBook = ModelHadithBooks.Data("0", "All books")
    var searchQuery = ""
    private var currentPageNo = 1
    private var totalPages: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentTopHadithBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun viewCreated() {
        initData()
    }

    override fun clicks() {
        binding.rvSearchHadith.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                if (dy > 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == showHadithData.size - 1) {
                        if (currentPageNo < totalPages) {
                            currentPageNo += 1
                            searchFromDb()
                        }
                    }
                }
            }
        })
        binding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (binding.etSearch.text.toString().trim().isNotEmpty()) {
                    showHadithData.clear()
                    currentPageNo = 1
                    searchFromDb()

                }
                return@setOnEditorActionListener true
            }
            false
        }

//        binding.etSearch.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(
//                charSequence: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                charSequence: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//            }
//
//            override fun afterTextChanged(editable: Editable?) {
//                // Cancel the previous search runnable if it exists
//                searchRunnable?.let { handler.removeCallbacks(it) }
//
//                // Define a new search runnable
//                searchRunnable = Runnable {
//                    if (!editable.isNullOrEmpty()) {
//                        val searchQuery = editable.toString().lowercase()
//                        when (selectedBook.id) {
//                            "0" -> {
//                                //search whole db
//                                viewModelHadith.hadithSearchAllBooks(searchQuery)
//                            }
//
//                            else -> {
//                                //search against book
//                                viewModelHadith.hadithSearchWithBookId(searchQuery, selectedBook.id)
//                            }
//                        }
//
//                    }
//                }
//
//                // Post the search runnable with a delay of 1 seconds
//                handler.postDelayed(searchRunnable!!, 1500)
//            }
//        })
    }

    private fun searchFromDb() {
        searchQuery = binding.etSearch.text.toString().trim().lowercase()
        when (selectedBook.id) {
            "0" -> {
                //search whole db
                viewModelHadith.hadithSearchAllBooks(currentPageNo, searchQuery)
            }

            else -> {
                //search against book
                viewModelHadith.hadithSearchWithBookId(currentPageNo, searchQuery, selectedBook.id)
            }
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterChapterHadiths(showHadithData, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {

                val intent = Intent(context, HadithDetailsActivity4::class.java)
                val currentPosition = showHadithData[position].id
                intent.putExtra("ayat_id", currentPosition)
                intent.putExtra("chapter_id", showHadithData[position].chapter_id)
                intent.putExtra("hadith_number", showHadithData[position].hadith_no)
                intent.putExtra("book_name", selectedBook.title)
                intent.putExtra("chapter_name", showHadithData[position].chapter_name)
                intent.putExtra("type", showHadithData[position].type)
                intent.putExtra("searchHadith", true)
                startActivity(intent)
            }

        }, "hadith", selectedBook.title)
        binding.rvSearchHadith.adapter = adapter

    }


    override fun initObservers() {
        super.initObservers()
        viewModel.hadithBooksLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    it.data?.data?.let { it1 -> booksList.addAll(it1) }
                    initData()
                    adapter.notifyDataSetChanged()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
        viewModelHadith.dbSearchHadithAllBooksLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {

                    totalPages = it.data?.total_pages!!
                    val oldSize = showHadithData.size
                    it.data?.let { it1 -> showHadithData.addAll(it1.data) }
                    if (oldSize == 0) {
                        initAdapter()
                    } else {
                        adapter.notifyItemRangeInserted(oldSize, booksList.size)
                    }


                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

        viewModelHadith.dbSearchHadithWithBookIdLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {

                    adapter.updateData(selectedBook.title)
                    totalPages = it.data?.total_pages!!
                    val oldSize = showHadithData.size
                    it.data?.let { it1 -> showHadithData.addAll(it1.data) }
                    if (oldSize == 0) {
                        initAdapter()
                    } else {
                        adapter.notifyItemRangeInserted(oldSize, booksList.size)
                    }


                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

    }

    override fun initData() {
        super.initData()
        hadithBooks.clear()
        hadithBooks.add(ModelHadithBooks.Data("0", "All books"))
        hadithBooks.addAll(booksList)
        val adapter: ArrayAdapter<ModelHadithBooks.Data> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hadithBooks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spHadithBooks.adapter = adapter



        SpinnerAdapterHelper.createAdapter(hadithBooks, binding.spHadithBooks) {
            selectedBook = hadithBooks[it]
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.getHadithBooks()
    }
}