package com.taibahai.models

import java.text.SimpleDateFormat
import java.util.Date

data class ModelComments (val ivProfile:Int,
                          val tvName:String,
                          val tvComment:String,
                          val tvCommentTiming: String
)

{

    companion object{
        fun Date.formatToHHmma(): String {
            val sdf = SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a")
            return sdf.format(this)
        }

        fun dateToMillis(dateString: String): Long {
            val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a")
            val date = dateFormat.parse(dateString)
            return date.time
        }
    }
}