package com.example.apptest.model.categoryHome

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class CategoryData(
    val Category: String,
    val Id: Int,
    val Videos:@RawValue List<Video>
):Parcelable