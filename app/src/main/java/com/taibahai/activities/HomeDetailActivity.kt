package com.taibahai.activities


import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelComments
import com.network.models.ModelHome
import com.network.network.NetworkResult
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterComments
import com.taibahai.databinding.ActivityHomeDetailBinding
import com.taibahai.utils.showOptionsMenu
import com.taibahai.utils.showToast

class HomeDetailActivity : BaseActivity() {
    lateinit var adapter: AdapterComments
    val showComments = mutableListOf<ModelComments>()
    lateinit var binding: ActivityHomeDetailBinding
    val viewModel: MainViewModelAI by viewModels()
    var comment = ""
    var model = ModelHome.Data()


    override fun onCreate() {
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {


        binding.ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        binding.ii.ivDots.setOnClickListener { view ->
            context.showOptionsMenu(view, R.menu.popup_report) {
                when (it.itemId) {
                    R.id.menu_report -> {
                        viewModel.feedReport(model.feed_id)
                        true
                    }

                    else -> false
                }
            }
        }

        binding.sendBtn.setOnClickListener {
            comment = binding.messageBox.text.toString()
            if (!comment.isNullOrEmpty()) {
                viewModel.feedComment(model.feed_id, comment)

            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.getFeedLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            //displayLoading(false)
            hideGone(binding.progressBar)
            when (it) {
                is NetworkResult.Loading -> {
                    // displayLoading(true)
                    show(binding.progressBar)
                }

                is NetworkResult.Success -> {
                    it.data?.data?.comments?.let { it1 -> showComments.addAll(it1) }
                    if (showComments.isNotEmpty()) {
                        adapter.notifyItemRangeInserted(0, showComments.size)
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
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showToast(it.data?.message.toString())
                    finish()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

        viewModel.simpleResponseLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            hideGone(binding.progressBar)
            when (it) {
                is NetworkResult.Loading -> {
                    show(binding.progressBar)
                }

                is NetworkResult.Success -> {
                    it.data?.message?.let { it1 -> showToast(it1) }
                    showComments.add(
                        0, ModelComments(
                            binding.messageBox.text.toString(),
                            currentUser?.name.toString(),
                            currentUser?.image.toString()
                        )
                    )
                    adapter.notifyItemInserted(0)
                    model.comments += 1
                    binding.ii.commentCounts.text = "${model.comments} Comments"
                    binding.messageBox.text.clear()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterComments(showComments)
        binding.rvComments.adapter = adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            model = intent.getSerializableExtra(AppConstants.BUNDLE) as ModelHome.Data
            binding.ii.data = model
        }
        viewModel.getFeed(model.feed_id)

    }


}