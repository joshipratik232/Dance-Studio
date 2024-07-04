package com.example.apptest.viewModel.seeAll

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SeeAllViewModelFactory(private val id: Int,
                             private val application: Application):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeeAllViewModel::class.java)) {
            return SeeAllViewModel(id, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}