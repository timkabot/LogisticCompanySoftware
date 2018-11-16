package com.example.timkabor.finallogisticcompany.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.divyanshu.draw.activity.DrawingActivity
import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.DaggerNetComponent
import com.example.timkabor.finallogisticcompany.MainView
import com.example.timkabor.finallogisticcompany.models.DispatchOrder
import com.example.timkabor.finallogisticcompany.presenters.MapActivityPresenter
import com.example.timkabor.finallogisticcompany.R
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.popup_order_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class MapActivity : MvpAppCompatActivity(), MainView, PermissionsListener, LocationEngineListener {
    override fun signatureDelivered(order_id: String) {
        Toast.makeText(this, "Signature delivered!", Toast.LENGTH_SHORT).show()
        this.finish()
    }

    override fun signatureSendFail() {
        Toast.makeText(this, "Failed in signature delivery! Signature saved in memory.", Toast.LENGTH_SHORT).show()
    }

    @InjectPresenter
    lateinit var presenter: MapActivityPresenter
    private val TAG = "MAPACTIVITY"

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originLocation: Location
    private lateinit var startDestinationButton: Button
    private lateinit var startSourceButton: Button
    private lateinit var bottomSlider: BottomSheetBehavior<LinearLayout>

    //nullable variables
    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null //ui display of location engine

    // variables for calculating and drawing a route
    private var destinationRoute: DirectionsRoute? = null
    private var sourceRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    // variables for order info
    private var orderTo: LatLng? = null
    private var orderFrom: LatLng? = null
    private var orderToPosition: Point? = null
    private var orderFromPosition: Point? = null
    private lateinit var order_id: String

    private val REQUEST_CODE_DRAW = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_slider)
        DaggerNetComponent.create().inject(presenter)

        initMap(savedInstanceState)
        initSlider()
        initNavigateButton()
        initSignatureButton()
        updateInfoAboutOrder()
    }

    // TODO: Do not touch please code to send real data from server!!!!! *&!@(&#*@!# besit.
    // TODO: Comment all mocks if you do it!!!!!!!!!!!!!
    // Mock: now all mocks will be made through model (as default values)
    override fun updateInfoAboutOrder() {
        val extras = intent.extras
        if (extras != null) {
            val order = extras.getParcelable<DispatchOrder>(DispatchOrder::class.java.canonicalName)

            orderTo = LatLng(order.destination_longitude, order.destination_latitude)
            orderFrom = LatLng(order.destination_longitude, order.destination_latitude)
            orderToPosition = Point.fromLngLat(order.destination_longitude, order.destination_latitude)
            orderFromPosition = Point.fromLngLat(order.source_longitude, order.source_latitude)


            destinationAddress.text = order.destination
            deliveryExpectationTime.text = order.delivery_window_start.toString()
            recipientPhone.text = order.phone_number
            servicePhone.text = order.phone_to_service
            order_id = order.id
//            presenter.getUser(order.deliver_to)

        } else
            Log.d(TAG, "Extras null!!1 Hence no needed info in activity!")
    }


    private val RECORD_REQUEST_CODE = 1337
    fun initSignatureButton() {
        askSignatureButton.setOnClickListener {
            run {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            RECORD_REQUEST_CODE)
                } else {
                    val intent = Intent(this, DrawingActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_DRAW)
                }
            }
        }
    }

    // Get bitmap in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {
                    val result = data.getByteArrayExtra("bitmap")
                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                    if (bitmap == null) {

                        Log.d(TAG, "Signature not received.")
                    } else {
                        Toast.makeText(this, "Sending signature...", Toast.LENGTH_SHORT).show()
                        presenter.sendSignature(bitmap, order_id)
                    }
                }
            }
        } else {
            Log.d(TAG, "Signature activity result is null!")
            // What todo if result null?
        }
    }

    fun initNavigateButton() {
        startDestinationButton = findViewById(R.id.startToDestinationButton)
        startDestinationButton.setOnClickListener {
            run {
                if (destinationRoute != null) {
                    val simulateRoute = true
                    val options = NavigationLauncherOptions.builder()
                            .directionsRoute(destinationRoute)
                            .shouldSimulateRoute(simulateRoute)
                            .build()
                    navigationMapRoute?.addRoute(destinationRoute)
                    // Call this method with Context from within an Activity
                    NavigationLauncher.startNavigation(this, options)
                } else {
                    Toast.makeText(this, "Please, wait route...", Toast.LENGTH_SHORT).show()
                }
            }

        }
        startSourceButton = findViewById(R.id.startToSourceButton)
        startSourceButton.setOnClickListener {
            run {
                if (sourceRoute != null) {
                    val simulateRoute = true
                    val options = NavigationLauncherOptions.builder()
                            .directionsRoute(sourceRoute)
                            .shouldSimulateRoute(simulateRoute)
                            .build()
                    navigationMapRoute?.addRoute(sourceRoute)
                    // Call this method with Context from within an Activity
                    NavigationLauncher.startNavigation(this, options)
                } else {
                    Toast.makeText(this, "Please, wait route...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initSlider() {
        bottomSlider = BottomSheetBehavior.from(bottom_sheet)
        bottomSlider.isHideable = false
        bottomSlider.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSlider.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                Log.e("onStateChanged", "onStateChanged:" + newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
                Log.e("onSlide", "onSlide")
            }
        })

    }

    fun initMap(savedInstanceState: Bundle?) {
        val token = getMapToken()
        if (token == null) {
            Toast.makeText(this, "Map token is null!", Toast.LENGTH_SHORT).show()
            return
        }
        Mapbox.getInstance(this, token)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { it ->
            map = it
            map.addMarker(MarkerOptions()
                    .position(orderTo)
                    .title("Order [To]"))
            map.addMarker(MarkerOptions()
                    .position(orderFrom)
                    .title("Order [From]"))
            enableLocation()
        }
    }

    fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()
            initializeLocationLayer()
        } else {
            //get users permissions
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun propertyTry(prior: LocationEnginePriority): Location? {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = prior
        locationEngine?.activate()
        return locationEngine?.lastLocation
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine() {
        var loc = propertyTry(LocationEnginePriority.NO_POWER)
        if (loc == null) {
            loc = propertyTry(LocationEnginePriority.BALANCED_POWER_ACCURACY)
            if (loc == null) {
                loc = propertyTry(LocationEnginePriority.LOW_POWER)
                if (loc == null) {
                    loc = propertyTry(LocationEnginePriority.HIGH_ACCURACY)
                    if (loc == null) {
                        Log.d(TAG, "Every priority property is NULL!!!")
                        Log.d(TAG, "Engine state (isConnected): " + locationEngine?.isConnected)
                        Log.d(TAG, "Engine state (interval): " + locationEngine?.interval)
                    }
                }
            }
        }
        val lastLocation = loc
        if (lastLocation != null) {
            originLocation = lastLocation
            setCameraPosition(lastLocation)
        } else {
            //if last location doesnt exist
            locationEngine?.addLocationEngineListener(this)
        }
    }

    private fun initializeLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView, map, locationEngine)
        locationLayerPlugin?.isLocationLayerEnabled = true
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL
    }

    private fun setCameraPosition(location: Location) {
        val originPosition = Point.fromLngLat(originLocation.longitude, originLocation.latitude)
        getRoute(originPosition, orderToPosition!!, { x: DirectionsRoute -> destinationRoute = x }, "Destination")
        getRoute(originPosition, orderFromPosition!!, { x: DirectionsRoute -> sourceRoute = x }, "Source")
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 13.0))


    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //show message why user needs to grant permission
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RECORD_REQUEST_CODE) {
            val intent = Intent(this, DrawingActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_DRAW)
        } else
            permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            originLocation = location
            setCameraPosition(location)
        }
    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun getRoute(origin: Point, destination: Point,
                         whereToPut: (DirectionsRoute) -> Unit, title: String) {
        Log.d(TAG, origin.toString() + " | " + destination.toString())
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        // You can get the generic HTTP info about the response
                        Timber.d("Response code: %s", response.code())
                        if (response.body() == null) {
                            Timber.e("No routes found, make sure you set the right user and access token.")
                            return
                        } else if (response.body()?.routes()!!.size < 1) {
                            Timber.e("No routes found")
                            return
                        }
                        whereToPut(response.body()!!.routes()[0])
                        // Draw the route on the map
                        if (navigationMapRoute == null) {
                            startDestinationButton.isEnabled = true
                            navigationMapRoute = NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute)
                        } //else {navigationMapRoute?.removeRoute()}
                        map.selectMarker(map.addMarker(MarkerOptions().position(LatLng(destination.latitude(),
                                destination.longitude())).title(title)))


                    }

                    override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                        Timber.e("Error: %s", throwable.message)
                    }
                })
    }

    fun getMapToken(): String? {
        //TODO:
        //return getString(R.string.map_token)
        val authToken = App.getAuthToken()
        if (authToken != null)
            return App.getMapToken()
        return null
    }

}
