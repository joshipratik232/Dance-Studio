package com.example.apptest.viewModel.image

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.authorization
import com.example.apptest.activities.Utils.Companion.contentType
import com.example.apptest.activities.Utils.Companion.token
import com.example.apptest.api.Api
import com.example.apptest.model.image.ImageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ImageViewModel : ViewModel() {
    private val _response = MutableLiveData<List<ImageData>>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getVideoDetails()
    }

    private fun getVideoDetails() {
        contentType["Content-Type"] = "application/json"
        authorization["Authorization"] = token
        coroutineScope.launch {

            val getVideoDeferred = Api.RETROFIT_SERVICE.getVideoAsync(Utils.clientKey, authorization, contentType)
            try {
                val listResult = getVideoDeferred.await()
                if (listResult.Success) {
                    _response.value = listResult.Data
                } else {
                    Timber.d("success false : + ${listResult.Message}")
                }
            } catch (t: Throwable) {
                Timber.d("error============> ${t.message.toString()}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}