package com.example.venomousboxer.disastermanagement

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class LiveStreamMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livestream_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        //style the map
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )

            if (!success) {
                Log.e("", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("", "Can't find style. Error: ", e)
        }

        // Add a marker Home and move the camera
        val home = LatLng(30.318890, 78.033612)
        val drone = arrayListOf(LatLng(30.318890, 78.033612),LatLng(30.089611, 78.266665)
            ,LatLng(29.952380, 78.166214),LatLng(30.473349, 78.066094))
        val zoom = 10
        for(i in 0..3){
            Log.d("DRONES : ","Lat : ${drone[i].latitude}, Lang : ${drone[i].longitude}")
            mMap.addMarker(
                MarkerOptions().position(drone[i]).title("Drone ${i+1}").icon(
                    BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_RED))).tag = "${i+1}"
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoom.toFloat()))

        // add on marker click listener
        mMap.setOnMarkerClickListener {
            val alert = AlertDialog.Builder(this)
            val tag = it.tag
            val alertMessageString = "Position - " + String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.position.latitude,
                it.position.longitude
            )
            alert.setTitle("Drone $tag")
            alert.setMessage(alertMessageString)
            alert.setPositiveButton("Back"){ dialog, _ -> dialog.cancel()}
            // TODO : replace this with intent to call chrome and show live stream
            alert.setNegativeButton("Go to live stream"){dialog, _-> dialog.cancel()}
            alert.create().show()
            false
        }
    }
}
