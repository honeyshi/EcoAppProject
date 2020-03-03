package com.example.ecoappproject.ui.map

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ecoappproject.R


class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val PERMISSION_REQUEST_COARSE_LOCATION = 101

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
            ViewModelProviders.of(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        // Ask for permissions
        activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_REQUEST_COARSE_LOCATION)

        mapView = root.findViewById(R.id.map_view_map_fragment)
        mapView.onCreate(savedInstanceState)

        mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync { mMap ->
            // Ask for permissions
            activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_COARSE_LOCATION)
            googleMap = mMap
            // For showing a move to my location button
            googleMap.isMyLocationEnabled = true
            // For dropping a marker at a point on the Map
            val sydney = LatLng(-34.0, 151.0)
            googleMap.addMarker(MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"))
            // For zooming automatically to the location of the marker
            val cameraPosition =
                CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}