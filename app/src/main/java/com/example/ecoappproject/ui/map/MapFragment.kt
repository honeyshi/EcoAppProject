package com.example.ecoappproject.ui.map

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecoappproject.LOCATIONS_DATABASE
import com.example.ecoappproject.MAP_FRAGMENT_TAG
import com.example.ecoappproject.OnSwipeTouchListener
import com.example.ecoappproject.R
import com.example.ecoappproject.items.LocationItem
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_map, container, false)

        // Make description for location template invisible
        val constraintLayoutLocationDescription =
            root.findViewById<ConstraintLayout>(R.id.constraint_layout_location_description)
        root.findViewById<ConstraintLayout>(R.id.constraint_layout_location_description).visibility =
            View.INVISIBLE

        // Set click listener for button - hide location description
        val hideLocationButton =
            root.findViewById<ImageButton>(R.id.image_button_hide_location_description)

        hideLocationButton.setOnClickListener {
            constraintLayoutLocationDescription.visibility = View.INVISIBLE
        }

        constraintLayoutLocationDescription.setOnTouchListener(object :
            OnSwipeTouchListener(requireActivity().applicationContext) {
            override fun onSwipeRight() {}

            override fun onSwipeLeft() {}

            override fun onSwipeBottom() {
                Log.w(MAP_FRAGMENT_TAG, "Swipe bottom.")
                constraintLayoutLocationDescription.visibility = View.INVISIBLE
            }

            override fun onSwipeTop() {}
        })

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

            // Get locations of eco points and put them on map
            //getLocationsFromDatabaseAndPutOnMap()

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
            /*val sydney = LatLng(56.259896, 43.882122)
            googleMap.addMarker(
                MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description")
                    .icon(
                        bitmapDescriptorFromVector(
                            requireActivity().applicationContext,
                            R.drawable.ic_map_pin_usual
                        )
                    )
            )*/
            // For zooming automatically to the location of the marker
            /*val cameraPosition =
                CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))*/
        }

        return root
    }

    private fun getLocationsFromDatabaseAndPutOnMap() {
        Log.w(MAP_FRAGMENT_TAG, "Get locations from database")
        FirebaseApp.initializeApp(requireContext().applicationContext)
        val locationsReference = FirebaseDatabase.getInstance().reference.child(LOCATIONS_DATABASE)
        locationsReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (location in dataSnapshot.children) {
                    val locationItem = location.getValue(LocationItem::class.java)

                    if (locationItem?.geo != null) {
                        Log.w(MAP_FRAGMENT_TAG, "Put location ${locationItem.geo} on map")
                        val locationCoordinateList = locationItem.geo.split(' ')
                        val locationCoordinate = LatLng(
                            locationCoordinateList[0].toDouble(),
                            locationCoordinateList[1].toDouble()
                        )
                        googleMap.addMarker(
                            MarkerOptions().position(locationCoordinate).title("Eco point").snippet("Eco point description")
                                .icon(
                                    bitmapDescriptorFromVector(
                                        requireActivity().applicationContext,
                                        R.drawable.ic_map_pin_usual
                                    )
                                )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(MAP_FRAGMENT_TAG, "Failed to read value.", error.toException())
            }
        })
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
            Log.w(MAP_FRAGMENT_TAG, "There are no permissions")
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
                Log.w(MAP_FRAGMENT_TAG, "Location permission is granted - getDeviceLocation()")
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
                        Log.w(MAP_FRAGMENT_TAG, "In location callback")
                        super.onLocationResult(locationResult)

                        if (locationResult?.lastLocation != null) {

                            val currentLocation = locationResult.lastLocation
                            Log.w(
                                MAP_FRAGMENT_TAG,
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
                            Log.w(MAP_FRAGMENT_TAG, "Stop getting location")
                            val removeTask =
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            removeTask.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.w(MAP_FRAGMENT_TAG, "Location Callback removed.")
                                } else {
                                    Log.w(MAP_FRAGMENT_TAG, "Failed to remove Location Callback.")
                                }
                            }

                            getLocationsFromDatabaseAndPutOnMap()

                        } else {
                            Log.w(MAP_FRAGMENT_TAG, "Current location is null. Using defaults.")
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