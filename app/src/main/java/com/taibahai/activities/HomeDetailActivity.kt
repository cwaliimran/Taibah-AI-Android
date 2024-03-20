package com.taibahai.activities


import android.app.Activity
import android.content.Intent
import androidx.activity.OnBackPressedCallback
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
    lateinit var binding: ActivityHomeDetailBinding
    lateinit var adapter: AdapterComments
    val showComments = mutableListOf<ModelComments>()
    val viewModel: MainViewModelAI by viewModels()
    var comment = ""
    var model = ModelHome.Data()


    override fun onCreate() {
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent().apply {
                    putExtra(AppConstants.BUNDLE, model)
                }
               setResult(Activity.RESULT_OK, intent)
               finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun clicks() {


        binding.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        binding.ii.tvLike.setOnClickListener {
            viewModel.putLike(model.feed_id)
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
                    } else {
                        show(binding.noData.root)
                    }
                    model.comments = showComments.size
                    binding.ii.commentCounts.text = "${model.comments} Comments"

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
                    showComments.add(
                        0, ModelComments(
                            binding.messageBox.text.toString(),
                            currentUser?.name.toString(),
                            currentUser?.image.toString()
                        )
                    )
                    hideGone(binding.noData.root)
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

        //only used for likes
        viewModel.likeLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    model.is_like = !model.is_like
                    if (model.is_like) {
                        model.likes += 1
                    } else {
                        model.likes -= 1
                    }

                    binding.ii.data = model
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