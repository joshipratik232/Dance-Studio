package com.example.apptest.viewModel.notify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.authorization
import com.example.apptest.activities.Utils.Companion.contentType
import com.example.apptest.activities.Utils.Companion.deviceId
import com.example.apptest.activities.Utils.Companion.fcmToken
import com.example.apptest.activities.Utils.Companion.notifyObject
import com.example.apptest.api.Api
import com.example.apptest.model.notify.NotifyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class Status { LOADING, DONE, ERROR }
class NotifyViewModel : ViewModel() {
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _list = MutableLiveData<List<NotifyData>>()
    val list: LiveData<List<NotifyData>>
        get() = _list

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _isOffline = MutableLiveData<Boolean>()
    val isOffline: LiveData<Boolean>
        get() = _isOffline

    private val _isOnline = MutableLiveData<Boolean>()
    val isOnline: LiveData<Boolean>
        get() = _isOnline

    init {
        _isOffline.value = false
        _isOnline.value = false
        getVideoDetails()
    }

    private fun getVideoDetails() {
        notifyObject.addProperty("Device", "android")
        notifyObject.addProperty("DeviceVersion", android.os.Build.MODEL)
        notifyObject.addProperty("DeviceId", deviceId)
        notifyObject.addProperty("FCMToken", fcmToken)

        coroutineScope.launch {
            _status.value = Status.LOADING
            val notifyListDeferred = Api.RETROFIT_SERVICE.notifyListAsync(Utils.clientKey, contentType, authorization, notifyObject)
            try {
                val result = notifyListDeferred.await()
                if (result.Success) {
                    _status.value = Status.DONE
                    Timber.d(("notify====>${result.Data}"))
                    _list.value = result.Data
                }
            } catch (t: Throwable) {
                Timber.d("Error======>${t.message}")
                _status.value = Status.ERROR
            }
        }
    }

    fun isOffline() {
        _isOffline.postValue(true)
    }

    fun isOnline() {
        getVideoDetails()
        _isOnline.postValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}