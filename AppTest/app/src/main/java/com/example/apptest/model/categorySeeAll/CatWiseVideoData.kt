package com.example.apptest.model.categorySeeAll

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatWiseVideoData(
    val Category: String,
    val Description: String,
    val Name: String,
    val ThumbLink: String,
    val VideoId: String,
    val VideoUrl: String,
    val LikeCount: String,
    val DisLikeCount:String,
    val ViewCount: String
):Parcelable