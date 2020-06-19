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
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecoappproject.LOCATIONS_DATABASE
import com.example.ecoappproject.MAP_FRAGMENT_TAG
import com.example.ecoappproject.classes.OnSwipeTouchListener
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener {
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
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102
    private val defaultZoom = 15f
    private var currentZoom = 12f

    // region UI elements
    private lateinit var constraintLayoutLocationDescription: ConstraintLayout
    private lateinit var textViewMonday: TextView
    private lateinit var textViewTuesday: TextView
    private lateinit var textViewWednesday: TextView
    private lateinit var textViewThursday: TextView
    private lateinit var textViewFriday: TextView
    private lateinit var textViewSaturday: TextView
    private lateinit var textViewSunday: TextView
    private lateinit var textViewCanRecycle: TextView
    private lateinit var textViewLocationAddress: TextView
    private lateinit var imageButtonMyLocation: ImageButton
    // endregion

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_map, container, false)

        // region Initialize UI elements
        constraintLayoutLocationDescription =
            root.findViewById(R.id.constraint_layout_location_description)
        constraintLayoutLocationDescription.visibility = View.INVISIBLE

        textViewMonday = root.findViewById(R.id.text_view_location_working_hours_monday)
        textViewTuesday = root.findViewById(R.id.text_view_location_working_hours_tuesday)
        textViewWednesday = root.findViewById(R.id.text_view_location_working_hours_wednesday)
        textViewThursday = root.findViewById(R.id.text_view_location_working_hours_thursday)
        textViewFriday = root.findViewById(R.id.text_view_location_working_hours_friday)
        textViewSaturday = root.findViewById(R.id.text_view_location_working_hours_saturday)
        textViewSunday = root.findViewById(R.id.text_view_location_working_hours_sunday)

        textViewCanRecycle = root.findViewById(R.id.text_view_location_can_recycle_text)
        textViewLocationAddress = root.findViewById(R.id.text_view_location_address)

        imageButtonMyLocation = root.findViewById(R.id.image_button_my_location)
        // endregion

        // region Location description constraint layout settings
        constraintLayoutLocationDescription.setOnTouchListener(object :
            OnSwipeTouchListener(requireActivity().applicationContext) {
            override fun onSwipeRight() {}

            override fun onSwipeLeft() {}

            override fun onSwipeBottom() {
                Log.w(MAP_FRAGMENT_TAG, "Swipe bottom.")
                val animate = TranslateAnimation(
                    0.0f,
                    0.0f,
                    0.0f,
                    constraintLayoutLocationDescription.height.toFloat()
                )
                animate.duration = 700
                constraintLayoutLocationDescription.startAnimation(animate)
                constraintLayoutLocationDescription.visibility = View.GONE
            }

            override fun onSwipeTop() {}
        })
        // endregion

        // region Set current day
        val calendar = Calendar.getInstance()
        val dayLongName =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                ?.toLowerCase(
                    Locale.ENGLISH
                )
        Log.w(MAP_FRAGMENT_TAG, "Current week day is $dayLongName")
        root.findViewWithTag<View>("view_location_current_day_$dayLongName").visibility =
            View.VISIBLE
        // endregion

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

            // Get location of eco points from database and put on map.
            getLocationsFromDatabaseAndPutOnMap()

            // Disable Google button for location as we use custom
            googleMap.uiSettings.isMyLocationButtonEnabled = false

            googleMap.setOnMarkerClickListener(this)

            // region Set on click listener for custom navigation buttons
            imageButtonMyLocation.setOnClickListener {
                getDeviceLocation()
            }

            root.findViewById<ImageButton>(R.id.image_button_zoom_plus).setOnClickListener {
                currentZoom = googleMap.cameraPosition.zoom
                if (currentZoom < googleMap.maxZoomLevel)
                    currentZoom += 1
                Log.w(MAP_FRAGMENT_TAG, "Current zoom is: $currentZoom")
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
            }

            root.findViewById<ImageButton>(R.id.image_button_zoom_minus).setOnClickListener {
                currentZoom = googleMap.cameraPosition.zoom
                if (currentZoom > googleMap.minZoomLevel)
                    currentZoom -= 1
                Log.w(MAP_FRAGMENT_TAG, "Current zoom is: $currentZoom")
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
            }
            // endregion
        }

        return root
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val markerTag = marker?.tag.toString()

        Log.w(MAP_FRAGMENT_TAG, "Click on marker with tag $markerTag. Swipe description")

        constraintLayoutLocationDescription.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0.0f,
            0.0f,
            constraintLayoutLocationDescription.height.toFloat(),
            0.0f
        )
        animate.duration = 700
        animate.fillAfter = true
        constraintLayoutLocationDescription.startAnimation(animate)

        FirebaseApp.initializeApp(requireContext().applicationContext)
        val locationReference =
            FirebaseDatabase.getInstance().reference.child(LOCATIONS_DATABASE).child(markerTag)
        locationReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val locationItem = dataSnapshot.getValue(LocationItem::class.java)
                Log.w(MAP_FRAGMENT_TAG, "For this marker location is ${locationItem?.geo}")
                textViewLocationAddress.text = locationItem?.address
                textViewCanRecycle.text = locationItem?.canRecycle
                textViewMonday.text = locationItem?.monday?.replace('_', '\n')
                textViewTuesday.text = locationItem?.tuesday?.replace('_', '\n')
                textViewWednesday.text = locationItem?.wednesday?.replace('_', '\n')
                textViewThursday.text = locationItem?.thursday?.replace('_', '\n')
                textViewFriday.text = locationItem?.friday?.replace('_', '\n')
                textViewSaturday.text = locationItem?.saturday?.replace('_', '\n')
                textViewSunday.text = locationItem?.sunday?.replace('_', '\n')
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(MAP_FRAGMENT_TAG, "Failed to read value.", error.toException())
            }
        })
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
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
                        // Add markers with tags in order to accesses them later on click
                        googleMap.addMarker(
                            MarkerOptions().position(locationCoordinate)
                                .icon(
                                    bitmapDescriptorFromVector(
                                        requireActivity().applicationContext,
                                        R.drawable.ic_map_pin_usual
                                    )
                                )
                        ).tag = location.key
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
            // If permission is already granted update UI to show user's location
            isLocationPermissionGranted = true
            updateLocationUI()
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_DENIED
            ) {
                // If permission is denied update user's UI for not showing location
                isLocationPermissionGranted = false
                updateLocationUI()
            } else {
                // If location is not granted - ask for permission
                Log.w(MAP_FRAGMENT_TAG, "There are no permissions")
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }
    }

    // Handle result of the permission request
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Log.w(MAP_FRAGMENT_TAG, "Handle result of locations permission request")
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
        Log.w(MAP_FRAGMENT_TAG, "Update location on request result")
        updateLocationUI()
    }

    // If the user has granted location permission, enable the My Location layer and the related control on the map,
    // otherwise disable the layer and the control, and set the current location to null
    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateLocationUI() {
        Log.w(
            MAP_FRAGMENT_TAG,
            "Start updating UI. Permission granted is $isLocationPermissionGranted"
        )
        try {
            if (isLocationPermissionGranted) {
                googleMap.isMyLocationEnabled = true
                imageButtonMyLocation.visibility = View.VISIBLE
            } else {
                googleMap.isMyLocationEnabled = false
                imageButtonMyLocation.visibility = View.INVISIBLE
            }
            getDeviceLocation()
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
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        currentLocation.latitude,
                                        currentLocation.longitude
                                    ), defaultZoom
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

                        } else {
                            Log.w(MAP_FRAGMENT_TAG, "Current location is null. Using defaults.")
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    defaultLocation,
                                    defaultZoom
                                )
                            )
                            imageButtonMyLocation.visibility = View.INVISIBLE
                        }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
                )
            } else {
                // If location is denied - move camera to default location
                Log.w(
                    MAP_FRAGMENT_TAG,
                    "Location permission is not granted - getDeviceLocation(). Show default location"
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        defaultLocation,
                        defaultZoom
                    )
                )
                imageButtonMyLocation.visibility = View.INVISIBLE
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