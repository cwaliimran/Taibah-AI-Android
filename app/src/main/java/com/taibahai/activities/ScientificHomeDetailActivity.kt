package com.taibahai.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.network.base.BaseActivity
import com.network.models.ModelComments
import com.network.models.ModelHome
import com.network.network.NetworkResult
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.utils.convertLongToDate
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterComments
import com.taibahai.databinding.ActivityHomeDetailBinding
import com.taibahai.databinding.ActivityScientificHomeDetailBinding
import com.taibahai.utils.showOptionsMenu
import com.taibahai.utils.showToast

class ScientificHomeDetailActivity : BaseActivity() {
    lateinit var binding: ActivityScientificHomeDetailBinding
    lateinit var adapter: AdapterComments
    val showComments = mutableListOf<ModelComments>()
    val viewModel: MainViewModelAI by viewModels()
    var comment = ""
    var model = ModelHome.Data()
    private var postType: String? = null



    override fun onCreate() {
        binding = ActivityScientificHomeDetailBinding.inflate(layoutInflater)
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
                    } else {
                        show(binding.noData.root)
                    }
                    model.comments = showComments.size

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
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
                            ModelComments(
                                binding.messageBox.text.toString(),
                                currentUser?.name.toString(),
                                currentUser?.image.toString(),
                                System.currentTimeMillis().convertLongToDate()
                            )
                        )
                        hideGone(binding.noData.root)
                        adapter.notifyItemInserted(showComments.size)
                        model.comments += 1
                        binding.messageBox.text.clear()

                    }

                    is NetworkResult.Error -> {
                        showToast(it.message.toString())
                    }
                }
            }

        }



        //only used for likes
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
            postType = intent.getStringExtra("POST_TYPE")
            Glide.with(this).load(model.feed_attachments.firstOrNull()?.file)
                .into(binding.ivUploadImage)
            binding.tvScientificDescription.text = Html.fromHtml(model.scientific_description, Html.FROM_HTML_MODE_COMPACT)
            binding.description.text = Html.fromHtml(model.description, Html.FROM_HTML_MODE_COMPACT)
        }
        viewModel.getFeed(model.feed_id)

    }


}