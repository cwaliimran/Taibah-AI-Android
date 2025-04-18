package com.taibahai.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.PopupWindow
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
import com.taibahai.activities.CreatePostActivity
import com.taibahai.activities.HomeDetailActivity
import com.taibahai.activities.LoginActivity
import com.taibahai.activities.NotificationActivity
import com.taibahai.activities.ScientificHomeDetailActivity
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.DialogAppTourBinding
import com.taibahai.databinding.DialogWelcomeBinding
import com.taibahai.databinding.FragmentHomeBinding
import com.taibahai.utils.AppTourDialog
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
            inflater, com.taibahai.R.layout.fragment_home, container, false
        )

        return binding.root

    }

    override fun viewCreated() {
        if (!AppClass.sharedPref.getBoolean("welcomeNote")) {
            AppClass.sharedPref.storeBoolean("welcomeNote", true)
            val dialog = Dialog(requireActivity())
            val layoutInflater = LayoutInflater.from(requireActivity())
            val binding = DialogWelcomeBinding.inflate(layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(binding.root)
            dialog.setCancelable(false)
            binding.btnyes.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )

            dialog.show()

        }

    }


    private val addPostActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                //app tour check
                if (data?.getStringExtra(AppConstants.BUNDLE) == "tour") {
                    return@registerForActivityResult
                }
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
            var isSilverPurchased =
                AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED)
            var isGoldPurchased =
                AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED)
            var isDiamondPurchased =
                AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED)

            if (isSilverPurchased || isGoldPurchased || isDiamondPurchased) {
                addPostActivityResultLauncher.launch(
                    Intent(
                        requireContext(),
                        CreatePostActivity::class.java
                    )
                )
            } else {
                //show toast to subscribe first
                showToast("Please subscribe to create a post.")
            }
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            mData.clear()
            sharedViewModel.setData(mData)
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
                    if (currentPageNo == 1) {
                        mData.clear()
                        adapter.notifyDataSetChanged()
                    }

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
                    try {
                        mData[currentItemAction].is_like = !mData[currentItemAction].is_like
                        if (mData[currentItemAction].is_like) {
                            mData[currentItemAction].likes += 1
                        } else {
                            mData[currentItemAction].likes -= 1
                        }
                        adapter.notifyItemChanged(currentItemAction)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("An error occurred while updating the like status.")
                    }
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
                    try {
                        adapter.notifyItemRemoved(reportedPos)
                        mData.removeAt(reportedPos)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("An error occurred while removing the item.")
                    }
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

                    "scientific_detail" -> {
                        val intent =
                            Intent(requireContext(), ScientificHomeDetailActivity::class.java)
                        intent.putExtra(AppConstants.BUNDLE, mData[position])
                        detailActivityResultLauncher.launch(intent)
                    }


                    else -> {}
                }
            }

        }, requireActivity()) { data, menuItem ->

            when (menuItem.itemId) {
                com.taibahai.R.id.menu_report -> {
                    reportedPos = mData.indexOf(data)
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

                if (data?.getStringExtra(AppConstants.BUNDLE) == "tour") {
                    AppTourDialog.appTour(
                        requireActivity(),
                        binding.ivCreatePostIcon,
                        "Create Post",
                        "On the Home screen, there is an  option to create a post. Clicking  this button will take you to the  Create Post screen."
                    ) {
                        addPostActivityResultLauncher.launch(
                            Intent(
                                requireContext(),
                                CreatePostActivity::class.java
                            )
                        )
                    }
                    return@registerForActivityResult
                }

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

    fun Activity.dialogAppTour(anchor: View, event: MotionEvent, modelTask: String) {
        val binding = DialogAppTourBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        //show on exact view position
//        // Calculate the x and y offsets for positioning the popup to the left
//        val xOff = -(popupWindow.width / 2)  // Adjust this value as needed
//        val yOff = 0 // Adjust this value as needed
//
//        // Show the popup to the left of the anchor view
//        popupWindow.showAsDropDown(anchor, xOff, yOff)

        // Calculate the x and y coordinates for positioning the popup
        val x = (event.rawX - popupWindow.width).toInt()
        val y = event.rawY.toInt()

        // Show the popup at the exact clicked position relative to the anchor view
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, x, y)


    }
}