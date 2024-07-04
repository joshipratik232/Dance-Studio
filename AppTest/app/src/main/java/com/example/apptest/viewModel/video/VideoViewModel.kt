package com.example.apptest.viewModel.video

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.authorization
import com.example.apptest.activities.Utils.Companion.contentType
import com.example.apptest.activities.Utils.Companion.deviceId
import com.example.apptest.activities.Utils.Companion.fcmToken
import com.example.apptest.activities.Utils.Companion.jsonObject
import com.example.apptest.activities.Utils.Companion.token
import com.example.apptest.api.Api
import com.example.apptest.model.categoryHome.Video
import com.example.apptest.model.categorySeeAll.CatWiseVideoData
import com.example.apptest.model.recentVideo.RecentVideoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class Status { LOADING, DONE, ERROR }
class VideoViewModel(@Suppress("UNUSED_PARAMETER") video: Video?, recentVideoData: RecentVideoData?, catWiseVideoData: CatWiseVideoData?, val app: Application) : AndroidViewModel(app) {

    private val _selectedData = MutableLiveData<String>()
    val selectedData: LiveData<String>
        get() = _selectedData

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val selectId = MutableLiveData<String>()

    private val _likeChange = MutableLiveData<String>()
    val likeChange: LiveData<String>
        get() = _likeChange

    private val _addToList = MutableLiveData<String>()
    val addToList: LiveData<String>
        get() = _addToList

    private val _isaddToList = MutableLiveData<Boolean>()
    val isaddToList: LiveData<Boolean>
        get() = _isaddToList

    private val _isLike = MutableLiveData<Boolean>()
    val isLike: LiveData<Boolean>
        get() = _isLike

    private val _isDislike = MutableLiveData<Boolean>()
    val isDislike: LiveData<Boolean>
        get() = _isDislike

    private val _recentvideo = MutableLiveData<List<RecentVideoData>>()
    val recentvideo: LiveData<List<RecentVideoData>>
        get() = _recentvideo

    private val _navigateToSelected = MutableLiveData<RecentVideoData>()
    val navigateToSelected: LiveData<RecentVideoData>
        get() = _navigateToSelected

    private val _videoLike = MutableLiveData<Int>()
    val videoLike: LiveData<Int>
        get() = _videoLike

    private val _videoName = MutableLiveData<String>()
    val videoName: LiveData<String>
        get() = _videoName

    private val _cat = MutableLiveData<String>()
    val cat: LiveData<String>
        get() = _cat

    private val _dislikeCount = MutableLiveData<Int>()
    val dislikeCount: LiveData<Int>
        get() = _dislikeCount

    private val _view = MutableLiveData<Int>()
    val view: LiveData<Int>
        get() = _view

    private val _videoDesc = MutableLiveData<String>()
    val videoDesc: LiveData<String>
        get() = _videoDesc

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _isOffline = MutableLiveData<Boolean>()
    val isOffline: LiveData<Boolean>
        get() = _isOffline

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        if (recentVideoData == null && catWiseVideoData == null) {
            selectId.value = video?.VideoId
            _selectedData.value = video?.VideoUrl
        } else if (video == null && catWiseVideoData == null) {
            selectId.value = recentVideoData!!.VideoId
            _selectedData.value = recentVideoData.VideoUrl
        } else {
            selectId.value = catWiseVideoData!!.VideoId
            _selectedData.value = catWiseVideoData.VideoUrl
        }
        Timber.d("videourl==>>>${_selectedData.value}")
        _isOffline.value = false
        getVideoDetails()
        getRecentVideos()
    }

    private fun getRecentVideos() {
        coroutineScope.launch {
            _status.value = Status.LOADING
            val recentVideoDeferred = Api.RETROFIT_SERVICE.getRecentVideoAsync(Utils.clientKey, contentType, authorization)
            try {
                val result = recentVideoDeferred.await()
                if (result.Success) {
                    _status.value = Status.DONE
                    _recentvideo.value = result.Data
                    Timber.d("recent::::::::${_recentvideo.value}")
                }
            } catch (t: Throwable) {
                Timber.d("recent video error : ${t.message.toString()}")
                _status.value = Status.ERROR
            }
        }
    }

    fun isOffline() {
        _isOffline.postValue(true)
    }

    fun isOnline() {
        _isOffline.postValue(false)
        getRecentVideos()
        getVideoDetails()
    }

    fun getVideoDetails() {
        jsonObject.addProperty("VideoId", selectId.value)
        jsonObject.addProperty("Device", "android")
        jsonObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        jsonObject.addProperty("DeviceId", deviceId)
        jsonObject.addProperty("FCMToken", fcmToken)

        coroutineScope.launch {
            contentType["Content-Type"] = "application/json"
            authorization["Authorization"] = token
            val videoDetailDeferred = Api.RETROFIT_SERVICE.videoDetailAsync(Utils.clientKey, contentType, authorization, jsonObject)
            try {
                val result = videoDetailDeferred.await()
                if (result.Success) {
                    for (data in result.Data) {
                        _videoName.value = data.Name
                        _videoLike.value = data.LikeCount
                        _dislikeCount.value = data.DisLikeCount
                        _videoDesc.value = data.Description
                        _isLike.value = data.IsLike
                        _isDislike.value = data.IsDislike
                        _isaddToList.value = data.IsMyList
                        _url.value = data.VideoUrl
                        _view.value = data.ViewCount
                        _cat.value = data.Category
                        Timber.d("_videoname===>${_videoName.value}")
                    }

                } else {
                    Timber.d("success false :  ${result.Message}")
                }
            } catch (t: Throwable) {
                Timber.d("error============> ${t.message.toString()}")
            }
        }
    }

    fun openVideo(video: RecentVideoData) {
        _navigateToSelected.value = video
    }

    fun navComplete() {
        _navigateToSelected.value = null
    }

    fun isLikeChange(makelike: Boolean) {
        jsonObject.addProperty("VideoId", selectId.value)
        jsonObject.addProperty("MakeLike", makelike)
        jsonObject.addProperty("Device", "android")
        jsonObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        jsonObject.addProperty("DeviceId", deviceId)
        jsonObject.addProperty("FCMToken", fcmToken)
        coroutineScope.launch {
            val likeChangeDeferred = Api.RETROFIT_SERVICE.likeChange(Utils.clientKey, contentType, authorization, jsonObject)
            try {
                val result = likeChangeDeferred.await()
                if (result.Success) {
                    for (data in result.Data) {
                        _likeChange.value = data.ResponseMessage
                    }
                }
            } catch (t: Throwable) {
                Timber.d("likeChange Error: ${t.stackTrace}")
            }
        }
    }

    fun addToList() {
        jsonObject.addProperty("VideoId", selectId.value)
        jsonObject.addProperty("Device", "android")
        jsonObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        jsonObject.addProperty("DeviceId", deviceId)
        jsonObject.addProperty("FCMToken", fcmToken)
        coroutineScope.launch {
            val addToListDeferred = Api.RETROFIT_SERVICE.addToList(Utils.clientKey, contentType, authorization, jsonObject)
            try {
                val result = addToListDeferred.await()
                if (result.Success) {
                    for (data in result.Data) {
                        _addToList.value = data.ResponseMessage
                    }
                }
            } catch (t: Throwable) {
                Timber.d("$t")
            }
        }
    }

    fun download(context: Context)/*: DownloadManager.Request*/ {
        /*val request: DownloadManager.Request = DownloadManager.Request(_selectedData.value!!.toUri().buildUpon().scheme("http").build())
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(false)
        request.setTitle(_videoName.value)
        request.setDescription(_videoDesc.value)
        request.setDestinationInExternalFilesDir(context, Environment.getDataDirectory().path + File.separator + "DStudio", "${_videoName.value}.mp4")
        return request*/
//        val downloadTask = com.example.apptest.downloadBackground.DownloadTask(context)
//        downloadTask.execute(_selectedData.value)
    }
}