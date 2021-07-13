package com.joule.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.geometry.LatLng

class MainViewModel : ViewModel() {

    private val _latlng = MutableLiveData<LatLng>().apply { value = null }
    val latlng: LiveData<LatLng> = _latlng

    fun setLatlng(latLng: LatLng?){
        _latlng.value = latLng
    }
}