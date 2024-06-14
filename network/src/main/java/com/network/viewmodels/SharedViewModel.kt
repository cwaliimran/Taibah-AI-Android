package com.network.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.network.models.ModelHome

class SharedViewModel : ViewModel() {
    private val _data = MutableLiveData<MutableList<ModelHome.Data>>()
    val data: LiveData<MutableList<ModelHome.Data>> get() = _data

    fun setData(newData: MutableList<ModelHome.Data>) {
        _data.value = newData
    }
}
