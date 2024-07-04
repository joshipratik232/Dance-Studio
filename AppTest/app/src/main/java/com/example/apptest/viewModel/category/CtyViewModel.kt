package com.example.apptest.viewModel.category

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.authorization
import com.example.apptest.activities.Utils.Companion.clientKey
import com.example.apptest.activities.Utils.Companion.contentType
import com.example.apptest.activities.Utils.Companion.fcmToken
import com.example.apptest.activities.Utils.Companion.jsonObject
import com.example.apptest.api.Api
import com.example.apptest.model.categoryHome.CategoryData
import com.example.apptest.model.categoryHome.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
enum class Status{LOADING, DONE, ERROR}
class CtyViewModel : ViewModel() {
    private val _navigateTo = MutableLiveData<Int>()
    val navigateTo: LiveData<Int>
        get() = _navigateTo

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
    get() = _status

    private val _navigateToSelected = MutableLiveData<Video>()
    val navigateToSelected: LiveData<Video>
        get() = _navigateToSelected

    private val _data = MutableLiveData<List<CategoryData>>()
    val categoryData: LiveData<List<CategoryData>>
        get() = _data

    private val _video = MutableLiveData<List<Video>>()
    val video: LiveData<List<Video>>
        get() = _video

    private val _isOffline = MutableLiveData<Boolean>()
    val isOffline : LiveData<Boolean>
    get() = _isOffline

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getCtyVideos()
        _isOffline.value = false
    }

    private fun getCtyVideos() {
        fcmToken = Utils.randomString(10)
        jsonObject.addProperty("Device", "android")
        jsonObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        jsonObject.addProperty("DeviceId", Utils.deviceId)
        jsonObject.addProperty("FCMToken", fcmToken)

        coroutineScope.launch {
            _status.value = Status.LOADING
            contentType["Content-Type"] = "application/json"
            val categoryDeferred = Api.RETROFIT_SERVICE.categoryVideoAsync(clientKey, contentType, authorization, jsonObject)
            try {
                val result = categoryDeferred.await()
                if (result.Success) {
                    _status.value = Status.DONE
                    _data.value = result.Data
                    for (data in result.Data) {
                        for (videoid in data.Videos) {
                            _video.value = data.Videos
                            Timber.d("videoid====>>>${videoid.ThumbLink}")
                        }
                    }
                }
            } catch (t: Throwable) {
                Timber.d("Error CtyViewModel : ${t.message}")
                _status.value = Status.ERROR
            }
        }
    }

    fun seeAllVideo(id: Int) {
        _navigateTo.value = id
    }

    fun navComp() {
        _navigateTo.value = null
    }

    fun navigateToComplete() {
        _navigateToSelected.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun isOffline() {
        _isOffline.postValue(true)
    }

    fun isOnline() {
        _isOffline.postValue(false)
        getCtyVideos()
    }
}