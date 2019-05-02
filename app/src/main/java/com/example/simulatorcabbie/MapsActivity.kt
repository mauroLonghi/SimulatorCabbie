package com.example.simulatorcabbie

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext)
        if (status == ConnectionResult.SUCCESS) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)
        } else {
            val dialog = GooglePlayServicesUtil.getErrorDialog(status, applicationContext as Activity, 10)
            dialog.show()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val uiSettings = mMap.uiSettings
        uiSettings.isZoomControlsEnabled = true // en false elimina la ubicacion actual

        val currentLocation = LatLng(lat_Initial, lng_Initial)
        mMap.addMarker(MarkerOptions().position(currentLocation).title("Marker in Tandil").icon(BitmapDescriptorFactory.defaultMarker()))


        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            //            mMap.moveCamera(CameraUpdateFactory.newLatLng(mMap.getMyLocation()));
            //            mMap.animateCamera(CameraUpdateFactory.zoomBy(20));
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()

            }
        }
    }

    private fun setMapMarkerLongClick(map: GoogleMap, bitmap: Bitmap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title("Conductor")
                    .snippet(snippet)
            )
        }
    }

    fun onClick(v: View) {
        if (v.id == R.id.button_search) {
            val i_location = findViewById<EditText>(R.id.editText)
            val location = i_location.text.toString()
            var addressList: List<Address>? = null
            val mark = MarkerOptions()
            if (location != "") {
                val geocoder = Geocoder(this)
                try {
                    addressList = geocoder.getFromLocationName(location, 5)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                for (i in addressList!!.indices) {
                    val myAddress = addressList[i]
                    val latLng = LatLng(myAddress.latitude, myAddress.longitude)
                    mark.position(latLng)
                    mark.title(myAddress.featureName)

                    mMap.addMarker(mark)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                }
            }
        }
        if (v.id == R.id.button_motorcycler) {
            val bitmap = decodeSampledBitmapFromResource(resources, R.drawable.scooter_image, SIZE_ICON, SIZE_ICON)
            setMapMarkerLongClick(mMap, bitmap)
        }
        if (v.id == R.id.button_van) {
            val bitmap = decodeSampledBitmapFromResource(resources, R.drawable.van_image, SIZE_ICON, SIZE_ICON)
            setMapMarkerLongClick(mMap, bitmap)
        }
        if (v.id == R.id.button_taxi) {
            val bitmap = decodeSampledBitmapFromResource(resources, R.drawable.taxi_image, SIZE_ICON, SIZE_ICON)
            setMapMarkerLongClick(mMap, bitmap)
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun decodeSampledBitmapFromResource(
        res: Resources, resId: Int,
        reqWidth: Int, reqHeight: Int
    ): Bitmap {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    companion object {
        private const val lat_Initial = -37.328242
        private const val lng_Initial = -59.136777
        private const val SIZE_ICON =35
    }
}
