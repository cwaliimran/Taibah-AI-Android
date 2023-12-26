package com.taibahai.activities


import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.network.base.BaseActivity
import com.network.models.ModelComments
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.adapters.AdapterComments
import com.taibahai.databinding.ActivityHomeDetailBinding
import com.taibahai.utils.showToast

class HomeDetailActivity : BaseActivity() {
    lateinit var adapter: AdapterComments
    val showComments = mutableListOf<ModelComments>()
    lateinit var binding: ActivityHomeDetailBinding
    val viewModel: MainViewModelAI by viewModels()
    var feedId = ""
    var comment = ""
    var noOfLikes=0
    var noOfComments=0
    var post=""


    override fun onCreate() {
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        binding.sendBtn.setOnClickListener {
            comment = binding.messageBox.text.toString()
            if(!comment.isNullOrEmpty()){
                viewModel.feedComment(feedId, comment)

            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.getFeedLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    binding.ii.tvUserName.text=it.data?.data?.user_name
                    binding.ii.tvDescription.text=it.data?.data?.description
                    binding.ii.tvTimesAgo.text=it.data?.data?.timesince
                    binding.ii.likesCounting.text= "${noOfLikes} Likes"
                    binding.ii.commentCounts.text= "${noOfComments} Comments"
                    Glide.with(this).load(post).into(binding.ii.ivUploadImage)
                    it?.data?.data?.comments?.let { it1 -> showComments.addAll(it1) }
                    adapter.notifyDataSetChanged()


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
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
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
            feedId = intent.getStringExtra("feedId").toString()
            noOfLikes = intent.getIntExtra("likes",0)
            noOfComments = intent.getIntExtra("comments",0)
            post = intent.getStringExtra("post").toString()
        }
        viewModel.getFeed(feedId)

    }


}