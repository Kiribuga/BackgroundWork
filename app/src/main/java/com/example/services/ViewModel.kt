package com.example.services

import android.app.Application
import android.util.Log
import androidx.work.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = Repository(application)
    private val downloadMLD = MutableLiveData<Operation>()

    val downloadLD: LiveData<Operation>
        get() = downloadMLD

    fun startDownload(url: String) {
        viewModelScope.launch {
            try {
                downloadMLD.postValue(repository.startDownload(url))
            } catch (t: Throwable) {
                Log.d("ViewModel", "Error download", t)
            }
        }
    }

    fun stopWork() {
        viewModelScope.launch {
            try {
                repository.stopDownload()
            } catch (t: Throwable) {
                Log.d("ViewModel", "Error stop work", t)
            }
        }
    }
}