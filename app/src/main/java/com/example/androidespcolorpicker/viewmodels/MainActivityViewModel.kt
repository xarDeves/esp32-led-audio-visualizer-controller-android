package com.example.androidespcolorpicker.viewmodels

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.androidespcolorpicker.helpers.Database
import com.example.androidespcolorpicker.views.ColorPalleteFragment
import com.example.androidespcolorpicker.views.ColorPickerFragment

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var fragments: Array<Fragment> = arrayOf(ColorPickerFragment(), ColorPalleteFragment())

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

    fun colorChangedFromPallete(color : Int){
        (fragments[0] as ColorPickerFragment).updateFromExternal(color)
    }

}