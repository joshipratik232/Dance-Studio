package com.example.apptest.model.recentVideo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecentVideoData(
        val Category: String,
        val Name: String,
        val ThumbLink: String,
        val VideoId: String,
        val VideoUrl: String,
        val LikeCount: String,
        val DisLikeCount:String,
        val ViewCount: String
) : Parcelable