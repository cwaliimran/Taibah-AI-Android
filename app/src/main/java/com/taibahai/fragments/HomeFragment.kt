package com.taibahai.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.models.ModelHome
import com.network.network.NetworkResult
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.isGuest
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.activities.CreatePostActivity
import com.taibahai.activities.HomeDetailActivity
import com.taibahai.activities.LoginActivity
import com.taibahai.activities.NotificationActivity
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.FragmentHomeBinding
import com.taibahai.utils.Constants
import com.taibahai.utils.genericDialog
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
    private var reportedPos: Int = 0


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
            if (isGuest()) {
                handleGuestLogic()
                return@setOnClickListener
            }
            val intent = Intent(requireContext(), CreatePostActivity::class.java)
            startActivity(intent)
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    totalPages = it.data?.total_pages!!
                    val oldSize = showList.size
                    feedId = it.data?.data?.firstOrNull()?.feed_id.toString()
                    showList.addAll((it.data?.data ?: listOf()))
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

        viewModel.likeLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    //  activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showList[currentItemAction].is_like = !showList[currentItemAction].is_like
                    if (showList[currentItemAction].is_like) {
                        showList[currentItemAction].likes += 1
                    } else {
                        showList[currentItemAction].likes -= 1
                    }
                    adapter.notifyItemChanged(currentItemAction)
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

        viewModel.reportFeedLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showToast(it.data?.message.toString())
                    adapter.notifyItemRemoved(reportedPos)
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

        viewModel.logoutLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    AppClass.sharedPref.clearAllPreferences()
                    startActivity(
                        Intent(
                            requireActivity(), LoginActivity::class.java
                        )
                    )
                    requireActivity().finishAffinity()
                }

                is NetworkResult.Error -> {
                    startActivity(
                        Intent(
                            requireActivity(), LoginActivity::class.java
                        )
                    )
                    requireActivity().finishAffinity()
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = AdapterHome(showList, isProfileFeed = false, object : OnItemClick {

            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                if (isGuest()) {
                    handleGuestLogic()
                    return
                }
                currentItemAction = position
                when (type) {
                    "like" -> {
                        if (data is String) {
                            viewModel.putLike(data)
                        }
                    }

                    "comment" -> {
                        val intent = Intent(requireContext(), HomeDetailActivity::class.java)
                        intent.putExtra(AppConstants.BUNDLE, showList[position])
                        detailActivityResultLauncher.launch(intent)

                    }

                    else -> {}
                }
            }

        }) { data, menuItem ->

            when (menuItem.itemId) {
                R.id.menu_report -> {
                    if (isGuest()) {
                        handleGuestLogic()
                    } else {
                        viewModel.feedReport(data.feed_id)
                    }
                    true
                }

                else -> false
            }
        }
        binding.rvHome.adapter = adapter
    }

    val detailActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result
                val data: Intent? = result.data
                var model: ModelHome.Data? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data?.getSerializableExtra(AppConstants.BUNDLE, ModelHome.Data::class.java)
                    } else {
                        data?.getSerializableExtra(AppConstants.BUNDLE) as ModelHome.Data
                    }

                if (model != null) {
                    showList[currentItemAction] = model
                    adapter.notifyItemChanged(currentItemAction)
                }
                // Extract data from the intent if needed
            }
        }


    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.home(pageno = 1)

    }

    val handleGuestLogic: () -> Unit = {
        activity?.genericDialog(object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                AppClass.sharedPref.clearAllPreferences()
                viewModel.logout(
                    AppClass.sharedPref.getString(Constants.DEVICE_ID, "").toString(),
                    "android"
                )
            }
        })
    }

}