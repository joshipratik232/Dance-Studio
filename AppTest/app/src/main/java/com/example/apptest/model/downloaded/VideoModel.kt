package com.example.apptest.model.downloaded

import android.graphics.Bitmap
import java.io.Serializable

class VideoModel : Serializable {
    var videoName: String? = null
    var filePath: String? = null
    var like: String? = null
    var dislike: String? = null
    var view: String? = null
    var category:String?=null
    var videoThumb: String? = null
    var isSelected: Boolean = false
}