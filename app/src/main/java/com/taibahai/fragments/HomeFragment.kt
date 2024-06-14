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
import androidx.fragment.app.activityViewModels
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
import com.network.viewmodels.SharedViewModel
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

    private val sharedViewModel: SharedViewModel by activityViewModels()
    var currentItemAction = -1

    lateinit var adapter: AdapterHome
    private var mData: MutableList<ModelHome.Data> = mutableListOf()
    val viewModel: MainViewModelAI by viewModels()
    private var currentPageNo = 1
    private var totalPages: Int = 0
    private var reportedPos: Int = 0
    private var isCalled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater, R.layout.fragment_home, container, false
        )

        return binding.root
    }

    override fun viewCreated() {
    }
    private val addPostActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val booleanResult = data?.getBooleanExtra("result_key", false) ?: false
                if (booleanResult) {
                    mData.clear()
                    adapter.notifyDataSetChanged()
                    viewModel.home(pageno = 1)
                }
            }
        }
    override fun clicks() {
        binding.ivCreatePostIcon.setOnClickListener {
            if (isGuest()) {
                handleGuestLogic()
                return@setOnClickListener
            }
            addPostActivityResultLauncher.launch(Intent(requireContext(), CreatePostActivity::class.java))
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            mData.clear()
            adapter.notifyDataSetChanged()
            viewModel.home(pageno = 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }


        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                if (dy > 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == mData.size - 1) {
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
                    isCalled = true
                    totalPages = it.data?.total_pages!!

                    mData.addAll((it.data?.data ?: listOf()))
                    sharedViewModel.setData(mData)

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

        sharedViewModel.data.observe(this) {
            if (it == null) {
                return@observe
            }
            val oldSize = mData.size
            mData = it
            if (oldSize == 0) {
                initAdapter()
            } else {
                adapter.notifyItemRangeInserted(oldSize, mData.size)
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
                    mData[currentItemAction].is_like = !mData[currentItemAction].is_like
                    if (mData[currentItemAction].is_like) {
                        mData[currentItemAction].likes += 1
                    } else {
                        mData[currentItemAction].likes -= 1
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
                    val aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)
                    AppClass.sharedPref.clearAllPreferences()
                    AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
                    AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)
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
        adapter = AdapterHome(mData, isProfileFeed = false, object : OnItemClick {

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
                        intent.putExtra(AppConstants.BUNDLE, mData[position])
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
                    mData[currentItemAction] = model
                    adapter.notifyItemChanged(currentItemAction)
                }
                // Extract data from the intent if needed
            }
        }


    override fun apiAndArgs() {
        super.apiAndArgs()
        if (isGuest()) {
            binding.ivNotification.visibility = View.GONE
        }

        if (!isCalled && sharedViewModel.data.value.isNullOrEmpty()) {
            viewModel.home(pageno = 1)
        } else {
            // If data is already available in SharedViewModel, use it to initialize the adapter
            sharedViewModel.data.value?.let {
                mData = it.toMutableList()
                initAdapter()
            }
        }

    }

    val handleGuestLogic: () -> Unit = {
        activity?.genericDialog(object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                val aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)
                AppClass.sharedPref.clearAllPreferences()
                AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
                AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)
                viewModel.logout(
                    AppClass.sharedPref.getString(Constants.DEVICE_ID, "").toString(),
                    "android"
                )
            }
        })
    }

}