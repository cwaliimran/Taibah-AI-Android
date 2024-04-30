package com.taibahai.search_database_tablayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.activities.SearchWholeQuranActivity
import com.taibahai.adapters.AdapterDbSearchQuran
import com.taibahai.databinding.FragmentTopQuranBinding
import com.taibahai.quran.SearchQuranChapterDetailActivity
import com.taibahai.quran.SurahListModel
import com.taibahai.utils.AppJsonUtils
import org.json.JSONArray
import org.json.JSONException

class TopQuranFragment : BaseFragment() {
    lateinit var binding: FragmentTopQuranBinding
    lateinit var adapter: AdapterDbSearchQuran
    val viewModel: MainViewModelAI by viewModels()
    val handler = Handler(Looper.getMainLooper())
    var searchRunnable: Runnable? = null

    private var mData: MutableList<SurahListModel> = mutableListOf()
    private var mDataFiltered: MutableList<SurahListModel> = mutableListOf()

    var jsonArr: JSONArray? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentTopQuranBinding>(
            inflater, R.layout.fragment_top_quran, container, false
        )

        return binding.getRoot()
    }

    override fun viewCreated() {
    }

    override fun clicks() {
        binding.searchWholeQuran.setOnClickListener {
            startActivity(Intent(requireContext(), SearchWholeQuranActivity::class.java))
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
            }


            override fun afterTextChanged(editable: Editable?) {
                // Cancel the previous search runnable if it exists
                searchRunnable?.let { handler.removeCallbacks(it) }

                // Define a new search runnable
                searchRunnable = Runnable {
                    mDataFiltered.clear()
                    adapter.notifyItemRangeRemoved(0, 0)

                    if (!editable.isNullOrEmpty()) {
                        val searchQuery = editable.toString().lowercase()
                        mData.filterTo(mDataFiltered) {
                                    it.transliteration_en.lowercase()
                                .contains(searchQuery) || it.translation_en.lowercase()
                                .contains(searchQuery) || it.number.lowercase()
                                .contains(searchQuery) || it.revelation_type.lowercase()
                                .contains(searchQuery)
                        }

                        adapter.notifyDataSetChanged()
                    } else {
                        mDataFiltered.addAll(mData)
                        adapter.notifyDataSetChanged()
                    }
                }

                // Post the search runnable with a delay of half second
                handler.postDelayed(searchRunnable!!, 500)
            }

        })
    }


    override fun initObservers() {
        super.initObservers()
        try {
            mData.clear()
            mDataFiltered.clear()
            jsonArr = JSONArray(AppJsonUtils.readRawResource(requireContext(), R.raw.allsurahlist))
            val gson = Gson()
            val type = object : TypeToken<List<SurahListModel?>?>() {}.type
            mData = gson.fromJson(jsonArr.toString(), type)
            mDataFiltered = gson.fromJson(jsonArr.toString(), type)

            adapter = AdapterDbSearchQuran(mDataFiltered, object : OnItemClick {
                override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                    super.onClick(position, type, data, view)
                    val intent = Intent(context, SearchQuranChapterDetailActivity::class.java)
                    intent.putExtra("ayat_id", mDataFiltered[position].id)
                    intent.putExtra("ayat_name", mDataFiltered[position].transliteration_en)
                    intent.putExtra("ayat_verse", mDataFiltered[position].total_verses)
                    intent.putExtra("ayat_type", mDataFiltered[position].revelation_type)
                    startActivity(intent)
                }
            })
            binding.rvSearchQuran.adapter = adapter


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }


}