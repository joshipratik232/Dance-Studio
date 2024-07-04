package com.example.apptest.model.categoryHome

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val Category: String,
    val CategoryId: Int,
    val Name: String,
    val ThumbLink: String,
    val VideoId: String,
    val VideoUrl: String,
    val LikeCount: String,
    val DisLikeCount:String,
    val ViewCount: String
):Parcelable