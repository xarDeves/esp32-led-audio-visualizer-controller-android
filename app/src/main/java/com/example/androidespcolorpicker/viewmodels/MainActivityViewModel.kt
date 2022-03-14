package com.example.androidespcolorpicker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.androidespcolorpicker.helpers.Database

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val data = MutableLiveData<MutableSet<Int>>()
    private val db: Database

    init {
        data.value = mutableSetOf()
        db = Database(application.filesDir)
        db.read(data.value!!)
    }

    fun write(data: MutableSet<Int>) {
        db.write(data)
    }

}