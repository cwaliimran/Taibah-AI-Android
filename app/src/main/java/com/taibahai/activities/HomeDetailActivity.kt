package com.taibahai.activities


import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterComments
import com.taibahai.databinding.ActivityHomeDetailBinding
import com.taibahai.models.ModelComments

class HomeDetailActivity : BaseActivity() {
    lateinit var adapter:AdapterComments
    val showComments=ArrayList<ModelComments>()
    lateinit var binding:ActivityHomeDetailBinding


    override fun onCreate() {
        binding= ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterComments(showComments)
        showComments.add(ModelComments(R.drawable.hassan,"Hussein","I really liked the one with “replace with name","6 min"))
        adapter.setDate(showComments)
        binding.rvComments.adapter=adapter
    }
}