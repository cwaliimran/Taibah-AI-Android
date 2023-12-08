package com.taibahai.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.activities.CreatePostActivity
import com.taibahai.adapters.AdapterHome
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.FragmentHomeBinding
import com.taibahai.models.ModelHome
import com.taibahai.models.ModelHomeSlider
import com.taibahai.utils.showToast


class HomeFragment : BaseFragment(),OnItemClick {
    lateinit var binding: FragmentHomeBinding


    lateinit var adapter: AdapterHome
    private var showList: MutableList<com.network.models.ModelHome.Data> = mutableListOf()
    val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater, R.layout.fragment_home, container, false
        )

        return binding.getRoot()
    }


    override fun viewCreated() {
        viewModel.home(pageno = 1)
    }

    override fun clicks() {
        binding.ivCreatePostIcon.setOnClickListener {
            val intent= Intent(requireContext(),CreatePostActivity::class.java)
            startActivity(intent)
        }
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
                    showList.addAll((it.data?.data ?: listOf()))
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = AdapterHome(this,showList)
        binding.rvHome.adapter = adapter


    }

    override fun onClick(position: Int, type: String?, data: Any?) {
        super.onClick(position, type, data)
        when (type) {
            "dots" -> {
                if (data != null) {
                    showPopupMenu(position, data)
                }
            }


            else -> {}
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showPopupMenu(position: Int, data: Any) {
        val popupView = layoutInflater.inflate(R.layout.item_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set up click listener for the menu item
      /*  val reportMenuItem = popupView.findViewById<View>(R.id.menu_report)
        reportMenuItem.setOnClickListener {
            // Handle menu item click here
            popupWindow.dismiss()
        }*/

        // Show the popup menu at a specific location
        val anchorView = view?.findViewById<View>(R.id.ivDots)
        popupWindow.showAsDropDown(anchorView, 0, 0)
    }




}