package com.example.apptest.model.videoDetail

data class VideoDetailData(
    val Category: String,
    val CategoryId: Int,
    val Description: String,
    val IsDownloaded: Boolean,
    val IsLike: Boolean,
    val DisLikeCount: Int,
    val IsDislike: Boolean,
    val IsMyList: Boolean,
    val LikeCount: Int,
    val ViewCount: Int,
    val Name: String,
    val PosterLink: String,
    val ThumbLink: String,
    val VideoId: String,
    val VideoUrl: String
)