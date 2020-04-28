package com.example.ecoappproject.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecoappproject.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var isLocationPermissionGranted by Delegates.notNull<Boolean>()

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e., how often you
    // should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var locationCallback: LocationCallback
    private val defaultLocation = LatLng(56.315470, 43.991542)
    private val PERMISSION_REQUEST_COARSE_LOCATION = 101
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_map, container, false)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity());

        mapView = root.findViewById(R.id.map_view_map_fragment)
        mapView.onCreate(savedInstanceState)

        mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync { mMap ->
            googleMap = mMap

            getLocationPermission()

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI()

            // Get the current location of the device and set the position of the map.
            getDeviceLocation()

            // Stop getting current location
            /*val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.w("Map Fragment", "Location Callback removed.")
                } else {
                    Log.w("Map fragment", "Failed to remove Location Callback.")
                }
            }*/

            // For dropping a marker at a point on the Map
            /*val sydney = LatLng(-34.0, 151.0)
            googleMap.addMarker(
                MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description")
                    .icon(
                        bitmapDescriptorFromVector(
                            requireActivity().applicationContext,
                            R.drawable.ic_map_pin_usual
                        )
                    )
            )
            // For zooming automatically to the location of the marker
            val cameraPosition =
                CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))*/
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
        */
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
        } else {
            Log.w("Map Fragment", "There are no permissions")
            activity?.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    // Handle result of the permission request
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        isLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    isLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    // If the user has granted location permission, enable the My Location layer and the related control on the map,
    // otherwise disable the layer and the control, and set the current location to null
    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateLocationUI() {
        try {
            if (isLocationPermissionGranted) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
        }
    }

    private fun getDeviceLocation() {
        /*
        * Get the best and most recent location of the device, which may be null in rare
        * cases when a location is not available.
        */
        try {
            if (isLocationPermissionGranted) {
                Log.w("Map Fragment", "Location permission is granted - getDeviceLocation()")
                locationRequest = LocationRequest().apply {
                    // Sets the desired interval for active location updates.
                    interval = TimeUnit.SECONDS.toMillis(60)

                    // Sets the fastest rate for active location updates. This interval is exact, and your
                    // application will never receive updates more frequently than this value.
                    fastestInterval = TimeUnit.SECONDS.toMillis(30)

                    // Sets the maximum time when batched location updates are delivered. Updates may be
                    // delivered sooner than this interval.
                    maxWaitTime = TimeUnit.MINUTES.toMillis(2)

                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        Log.w("Map Fragment", "In location callback")
                        super.onLocationResult(locationResult)

                        if (locationResult?.lastLocation != null) {

                            val currentLocation = locationResult.lastLocation
                            Log.w(
                                "Map Fragment",
                                "Current location is not null. Move camera to users location"
                            )
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        currentLocation.latitude,
                                        currentLocation.longitude
                                    ), 15f
                                )
                            )
                            Log.w("Map Fragment","Stop getting location"
                            )
                            val removeTask =
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            removeTask.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.w("Map Fragment", "Location Callback removed.")
                                } else {
                                    Log.w("Map fragment", "Failed to remove Location Callback.")
                                }
                            }

                        } else {
                            Log.w("Map Fragment", "Current location is null. Using defaults.")
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    defaultLocation,
                                    15f
                                )
                            )
                            googleMap.uiSettings.isMyLocationButtonEnabled = false
                        }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
                )
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
        }
    }

    // Create bitmap from image asset
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
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