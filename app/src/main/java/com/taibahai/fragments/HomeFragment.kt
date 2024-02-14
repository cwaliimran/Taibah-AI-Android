package com.taibahai.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.activities.CreatePostActivity
import com.taibahai.activities.NotificationActivity
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.FragmentHomeBinding
import com.taibahai.utils.showToast


class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding
    var feedId = ""
    var currentItemAction = -1

    lateinit var adapter: AdapterHome
    private var showList: MutableList<com.network.models.ModelHome.Data> = mutableListOf()
    val viewModel: MainViewModelAI by viewModels()
    private var currentPageNo = 1
    private var totalPages: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater, R.layout.fragment_home, container, false
        )

        return binding.getRoot()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun viewCreated() {
    }

    override fun clicks() {
        binding.ivCreatePostIcon.setOnClickListener {
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivity(intent)
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.rvHome.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                if (dy > 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == showList.size - 1) {
                        if (currentPageNo < totalPages) {
                            currentPageNo += 1
                            viewModel.home(pageno = currentPageNo)
                        }
                    }

                }
            }
        })
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.homeLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    feedId = it.data?.data?.firstOrNull()?.feed_id.toString()
                    showList.addAll((it.data?.data ?: listOf()))
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

        //only used for likes
        viewModel.simpleResponseLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    it.data?.message?.let { it1 -> showToast(it1) }
                    if (showList[currentItemAction].likes==1){
                        showList[currentItemAction].likes == 0
                        showList[currentItemAction].likes == 0
                    }else{
                        showList[currentItemAction].likes == 1
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = AdapterHome(showList, isProfileFeed = false, object : OnItemClick {

            override fun onClick(position: Int, type: String?, data: Any?) {
                currentItemAction = position
                when (type) {
                    "dots" -> {
                        if (data is String) {
                            showPopupMenu(position, data)
                        }
                    }

                    "like" -> {
                        if (data is String) {
                            viewModel.putLike(data)
                        }
                    }

                    else -> {}
                }
            }

        })
        binding.rvHome.adapter = adapter
    }


    @SuppressLint("MissingInflatedId")
    private fun showPopupMenu(position: Int, data: String) {
        val popupView = layoutInflater.inflate(R.layout.item_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isOutsideTouchable = true

        val reportMenuItem = popupView.findViewById<View>(R.id.menuReport)
        reportMenuItem.setOnClickListener {
            viewModel.feedReport(data)
            popupWindow.dismiss()
        }

        // Get the anchor view from the clicked item in the adapter
        val anchorView =
            binding.rvHome.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<View>(
                R.id.ivDots
            )

        // Show the popup menu at a specific location
        anchorView?.let {
            popupWindow.showAsDropDown(it, 0, 0)
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.home(pageno = 1)


    }


}