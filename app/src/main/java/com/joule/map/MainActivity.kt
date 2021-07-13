package com.joule.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.joule.map.Utils.PermissionUtils
import com.joule.map.databinding.ActivityMainBinding
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.btnMyLocate.setOnClickListener { myLocation(mapboxMap.style!!) }

        viewModel.latlng.observe(this, {
            it?.let {
                showLoading(false)
                binding.tvLat.text = "Lat : ${it.latitude}"
                binding.tvLng.text = "Lng : ${it.longitude}"
            }
        })


    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            val cameraPosition = CameraPosition.Builder()
                .zoom(18.0)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 800)
            mapboxMap.addOnMoveListener(object : MapboxMap.OnMoveListener {
                override fun onMoveBegin(detector: MoveGestureDetector) {
                    showLoading(true)
                }

                override fun onMove(detector: MoveGestureDetector) {
                }

                override fun onMoveEnd(detector: MoveGestureDetector) {
                    viewModel.setLatlng(mapboxMap.cameraPosition.target)
                }
            })

            myLocation(it)
        }

    }

    @SuppressLint("MissingPermission")
    fun myLocation(loadedMapstyle: Style) {
        showLoading(true)
        if (PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            val locationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.Builder(this, loadedMapstyle).build()
            )
            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.COMPASS
            viewModel.setLatlng(locationComponent.lastKnownLocation?.let { LatLng(locationComponent.lastKnownLocation!!.latitude, it.longitude) })

        } else {
            PermissionUtils.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(
                    permissions,
                    grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                myLocation(mapboxMap.style!!)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun showLoading(state: Boolean){
        if (state){
            binding.tvLng.visibility = View.GONE
            binding.tvLat.visibility = View.GONE
            binding.pbLatlng.visibility = View.VISIBLE
        }else{
            binding.tvLng.visibility = View.VISIBLE
            binding.tvLat.visibility = View.VISIBLE
            binding.pbLatlng.visibility = View.GONE
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


}