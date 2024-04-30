package com.taibahai.utils

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner


object SpinnerAdapterHelper {

    fun <T> createAdapter(
        dataList: List<T>,
        spinner: Spinner,
        onSelect: (Int) -> Unit
    ): ArrayAdapter<T> {
        val context = spinner.context
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, dataList)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                onSelect(i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        return adapter
    }
}