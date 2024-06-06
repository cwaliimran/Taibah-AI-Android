package com.taibahai.models

data class ModelUpgrade(val tvCount:String,
                        val title:String,
                        val packageName:String,
                        val upgradeList:ArrayList<ModelUpgradeList>,
                        var subscriptionPrice:String,
                        val isPurchased: Boolean,

)