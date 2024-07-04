package com.example.apptest.viewModel.seeAll

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.authorization
import com.example.apptest.activities.Utils.Companion.contentType
import com.example.apptest.activities.Utils.Companion.jsonObject
import com.example.apptest.api.Api
import com.example.apptest.model.categorySeeAll.CatWiseVideoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
enum class Status{LOADING, DONE, ERROR}
class SeeAllViewModel(@Suppress("UNUSED_PARAMETER") id: Int, application: Application) : AndroidViewModel(application) {
    private val _catId = MutableLiveData<Int>()
    val catId: LiveData<Int>
        get() = _catId

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _catName = MutableLiveData<String>()
    val catName : LiveData<String>
    get() = _catName

    private val _seeAllData = MutableLiveData<List<CatWiseVideoData>>()
    val seeAllData: LiveData<List<CatWiseVideoData>>
        get() = _seeAllData

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navToSelected = MutableLiveData<CatWiseVideoData>()
    val navToSelected: LiveData<CatWiseVideoData>
        get() = _navToSelected

    private val _isOffline = MutableLiveData<Boolean>()
    val isOffline : LiveData<Boolean>
        get() = _isOffline

    init {
        _status.value = Status.LOADING
        _isOffline.value = false
        _catId.value = id
        getCatWiseVideos()
    }

    private fun getCatWiseVideos() {
        jsonObject.addProperty("CategoryId", _catId.value)
        jsonObject.addProperty("Device", "android")
        jsonObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        jsonObject.addProperty("DeviceId", Utils.deviceId)
        jsonObject.addProperty("FCMToken", Utils.fcmToken)

        coroutineScope.launch {
            _status.value = Status.LOADING
            val catWiseVideoDeferred = Api.RETROFIT_SERVICE.catWiseVideoAsync(Utils.clientKey, contentType, authorization, jsonObject)
            try {
                val result = catWiseVideoDeferred.await()
                if (result.Success) {
                    _status.value = Status.DONE
                    _seeAllData.value = result.Data
                    for (data in result.Data){
                        _catName.value = data.Category
                    }
                } else {
                    Timber.d("Success failed")
                }
            } catch (t: Throwable) {
                Timber.d(t)
                _status.value = Status.ERROR
            }
        }
    }
    fun isOffline() {
        _isOffline.postValue(true)
    }

    fun isOnline() {
        _isOffline.postValue(false)
        getCatWiseVideos()
    }

    fun openSeeVideo(video: CatWiseVideoData?) {
        _navToSelected.value = video
    }

    fun navComplete() {
        _navToSelected.value = null
    }
}