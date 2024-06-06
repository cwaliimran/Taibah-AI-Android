package com.taibahai.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.network.utils.AppConstants.TBL_PURCHASES

@Entity(tableName = TBL_PURCHASES)
data class InAppPurchase(
    @PrimaryKey
    var id: String = "",
    var is_active: Boolean = false,
//    var date: Int? = 0,
//    var purchased_from: String? = "",
)