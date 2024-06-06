package com.taibahai.billings

import com.android.billingclient.api.ProductDetails

interface ProductsInterface {
    fun productsFetched(products: MutableList<ProductDetails> = mutableListOf()) {}
}