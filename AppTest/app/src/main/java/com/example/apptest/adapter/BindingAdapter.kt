package com.example.apptest.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.R
import com.example.apptest.activities.Utils
import com.example.apptest.model.categoryHome.CategoryData
import com.example.apptest.model.categoryHome.Video
import com.example.apptest.model.categorySeeAll.CatWiseVideoData
import com.example.apptest.model.downloaded.VideoModel
import com.example.apptest.viewModel.category.Status
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

@SuppressLint("SetTextI18n")
@BindingAdapter("cattext")
fun cattext(textView: TextView, text: String?) {
    textView.text = ": " + text.toString()
}

@BindingAdapter("setBitmap")
fun setBitmap(imageView: ImageView, string: String?) {
    imageView.setImageBitmap(Utils.decodeBitmap(string!!))
}

@BindingAdapter("downloadList")
fun downloadList(recyclerView: RecyclerView, data: List<VideoModel>?) {
    val adapter = recyclerView.adapter as DownloadVideoAdapter
    adapter.submitList(data)
}

@BindingAdapter("recentimageUrl")
fun loadRecentImage(imgview: ImageView, imgurl: String?) {
    imgurl?.let {
        val imageUri = imgurl.toUri().buildUpon().scheme("http").build()
        /*Glide.with(imgview.context).load(imageUri)
                .transform(RoundedCorners(radius).apply { RequestOptions().override(500,300) })
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgview)*/

        Picasso.get().load(imageUri).centerCrop()
                .transform(RoundedCornersTransformation(4, 4))
                .resize(120, 80)
                .placeholder(R.drawable.loading_see_all_animation)
                .error(R.drawable.ic_broken_image)
                .into(imgview)
    }

}

@BindingAdapter("imageUrl")
fun loadImage(imgview: ImageView, imgurl: String?) {
    imgurl?.let {
        val imageUri = imgurl.toUri().buildUpon().scheme("http").build()
        /*Glide.with(imgview.context).load(imageUri)
                .transform(RoundedCorners(radius).apply { RequestOptions().override(500,300) })
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgview)*/

        Picasso.get().load(imageUri).transform(RoundedCornersTransformation(8, 16)).resize(400, 400)
                .placeholder(R.drawable.loading_home_animation)
                .error(R.drawable.ic_broken_image)
                .into(imgview)
    }
}

@BindingAdapter("seeAllStatus")
fun seeAllStatus(imageView: ImageView, status: com.example.apptest.viewModel.seeAll.Status?) {
    status?.let {
        when (status) {
            com.example.apptest.viewModel.seeAll.Status.LOADING -> {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.loading_see_all_animation)
            }
            com.example.apptest.viewModel.seeAll.Status.DONE -> {
                imageView.visibility = View.GONE
            }
            com.example.apptest.viewModel.seeAll.Status.ERROR -> {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.ic_broken_image)
            }
        }
    }
}

@BindingAdapter("status")
fun status(imageView: ImageView, status: Status?) {
    when (status) {
        Status.LOADING -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.loading_home_animation)
        }
        Status.DONE -> {
            imageView.visibility = View.GONE
        }
        Status.ERROR -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("listImage")
fun bindRecyclerView(recyclerView: RecyclerView, video: List<Video>?) {
    val adapter = recyclerView.adapter as CategoryVideoAdapter
    adapter.submitList(video)
}

@BindingAdapter("seeAll")
fun seeAll(recyclerView: RecyclerView, catData: List<CatWiseVideoData>?) {
    val adapter = recyclerView.adapter as CatWiseSeeAllAdapter
    adapter.submitList(catData)
}

@BindingAdapter("seeAllImage")
fun seeAllImage(imageView: ImageView, url: String?) {
    url?.let {
        val imageUri = url.toUri().buildUpon().scheme("http").build()
        /*Glide.with(imageView.context).load(imageUri)
                .transform(RoundedCorners(10))
                .apply(RequestOptions()
                        .override(1000,200)
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imageView)*/

        Picasso.get().load(imageUri).centerCrop().transform(RoundedCornersTransformation(8, 8)).resize(500, 400)
                .placeholder(R.drawable.loading_see_all_animation)
                .error(R.drawable.ic_broken_image)
                .into(imageView)
    }
}

@BindingAdapter("listData")
fun bindRecycler(recyclerView: RecyclerView, data: List<CategoryData>?) {
    data?.let {
        val adapter = recyclerView.adapter as CategoryNameAdapter
        adapter.submitList(data)
    }
}

@BindingAdapter("listRecentVideos")
fun recentVideos(recyclerView: RecyclerView, data: List<com.example.apptest.model.recentVideo.RecentVideoData>?) {
    data?.let {
        val adapter = recyclerView.adapter as RecentVideoAdapter
        adapter.submitList(data)
    }
}

@BindingAdapter("title")
fun title(textView: TextView, categoryData: CategoryData?) {
    categoryData?.let {
        textView.text = categoryData.Category
    }
}

@BindingAdapter("listNotify")
fun listNotify(recyclerView: RecyclerView, data: List<com.example.apptest.model.notify.NotifyData>?) {
    val adapter = recyclerView.adapter as NotifyAdapter
    adapter.submitList(data)
}

@BindingAdapter("likes")
fun likeCount(textView: TextView, likes: Int?) {
    likes?.let {
        textView.text = likes.toString()
    }
}

@BindingAdapter("videoInfo")
fun videoInfo(textView: TextView, info: String?) {
    info?.let {
        textView.text = info
    }
}
