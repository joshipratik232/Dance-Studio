package com.example.apptest.model.image

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class ImageData(
    val Category: String,
    val Name: String,
    val ThumbLink: String,
    val VideoId: String
): Parcelable