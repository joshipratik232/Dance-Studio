package com.example.apptest.viewModel.download

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class DownloadViewModel() : ViewModel() {
    private val _filePath = MutableLiveData<String>()
    val filePath: LiveData<String>
        get() = _filePath

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    private val _noDownFlag = MutableLiveData<Boolean>()
    val noDownFlag: LiveData<Boolean>
        get() = _noDownFlag

    init {
        _noDownFlag.value = false
    }

    fun setDownFlag(){
        _noDownFlag.value = true
    }
}