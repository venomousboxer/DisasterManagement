package com.example.venomousboxer.disastermanagement

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    lateinit var button : Button

    data class Coordinate(var lat : Double, var lon : Double)

    var listOfCoordinate = mutableListOf<Coordinate>()

    lateinit var polygonForDroneScoutingArea : Polygon
    var drawnAlready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        button=findViewById(R.id.button_scout)
        button.setOnClickListener {
            if(listOfCoordinate.size>2){
                // make request to server for scouting and send the data items as json
                drawOnMap()
                // finish activity
//                finish()
            }else{
                Toast.makeText(this,"Choose more point to assign region for scouting",Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

//    private fun setPoiClick(map: GoogleMap){
//        map.setOnPoiClickListener {
//            val poiMarker = map.addMarker(MarkerOptions().position(it.latLng).title(it.name)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
//            poiMarker.tag = "poi"
//            poiMarker.showInfoWindow()
//        }
//    }

    private fun setMapLongClick(map: GoogleMap){
        map.setOnMapLongClickListener {
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.latitude,
                it.longitude
            )
            listOfCoordinate = (listOfCoordinate + Coordinate(it.latitude, it.longitude)).toMutableList()
            map.addMarker(MarkerOptions().position(it).title("Marker").snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

//    private fun getBitmapDescriptor(id: Int): BitmapDescriptor {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val vectorDrawable = getDrawable(id) as VectorDrawable?
//
//            val h = vectorDrawable!!.intrinsicHeight
//            val w = vectorDrawable.intrinsicWidth
//
//            vectorDrawable.setBounds(0, 0, w, h)
//
//            val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(bm)
//            vectorDrawable.draw(canvas)
//
//            return BitmapDescriptorFactory.fromBitmap(bm)
//
//        } else {
//            return BitmapDescriptorFactory.fromResource(id)
//        }
//    }

    private fun drawOnMap(){

        if(drawnAlready)polygonForDroneScoutingArea.remove()
        // Draw outline
//        var size = listOfCoordinate.size
//        size-=1
//        for( i in 1..size ){
//            val st = listOfCoordinate[i-1]
//            val en = listOfCoordinate[i]
//            val line = mMap.addPolyline(PolylineOptions().add(LatLng(st.lat,st.lon), LatLng(en.lat,en.lon)))
//            line.width = 5.0f
//            line.color = Color.RED
//        }
//

        // Color polygon
        val polygon = PolygonOptions()
        val list = mutableListOf<LatLng>()
        for(ind in listOfCoordinate.indices){
            val obj = listOfCoordinate[ind]
            polygon.add(LatLng(obj.lat, obj.lon))
            list.plusAssign(LatLng(obj.lat, obj.lon))
        }
        polygon.strokeColor(Color.RED)
        polygon.strokeWidth(0.30f)
        polygon.fillColor(Color.argb(187, 255, 170, 0))
        polygonForDroneScoutingArea = mMap.addPolygon(polygon)
//        Toast.makeText(this,"Drawing done", Toast.LENGTH_SHORT).show()
        drawnAlready = true

        val area = SphericalUtil.computeArea(list)
        val time = (area * 0.000247105) * 20
        val alert = AlertDialog.Builder(this)
        val alertMessageString = "Area - $area Meter Square \nTime Required - $time Seconds"
        alert.setTitle("Scouting Area")
        alert.setMessage(alertMessageString)
        alert.setPositiveButton("Back"){ dialog, _ -> dialog.cancel()}
        alert.setNegativeButton("Assign Drones"){dialog, _->
            Toast.makeText(this,"Drones Assigned for scouting", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        alert.create().show()
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

        // style map
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        //style the map
//        try {
//            // Customize the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            val success = mMap.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                    this, R.raw.map_style
//                )
//            )
//
//            if (!success) {
//                Log.e("", "Style parsing failed.")
//            }
//        } catch (e: Resources.NotFoundException) {
//            Log.e("", "Can't find style. Error: ", e)
//        }

        // add ground overlay
//        val pointer1 = LatLng(28.676999,77.090796)
//        val pointer2 = LatLng(28.637127,77.221988)
//        val cOverlay1 = GroundOverlayOptions()
//            .image(getBitmapDescriptor(R.drawable.ic_menu_send)).position(pointer1, 10000F).transparency(0.3F)
//        mMap.addGroundOverlay(cOverlay1)
//        val cOverlay2 = GroundOverlayOptions()
//            .image(getBitmapDescriptor(R.drawable.ic_menu_share)).position(pointer2, 10000F).transparency(0.3F)
//        mMap.addGroundOverlay(cOverlay2)


        // Add a marker Home and move the camera
        val home = LatLng(30.318890, 78.033612)
        val zoom = 10
        mMap.addMarker(MarkerOptions().position(home).icon(BitmapDescriptorFactory.defaultMarker
            (BitmapDescriptorFactory.HUE_RED)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoom.toFloat()))

        // call utility functions
        setMapLongClick(mMap)
//        setPoiClick(mMap)


        // setup map click listener
        mMap.setOnMarkerClickListener {
            val alert = AlertDialog.Builder(this)
            val alertMessageString = "Position - " + String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.position.latitude,
                it.position.longitude
            )
            alert.setTitle("Marker")
            alert.setMessage(alertMessageString)
            alert.setPositiveButton("Back"){ dialog, _ -> dialog.cancel()}
            alert.setNegativeButton("Delete Marker"){dialog, _->
                for(ind in listOfCoordinate.indices){
                    val temp = listOfCoordinate[ind]
                    if(it.position.latitude == temp.lat && it.position.longitude == temp.lon){
                        listOfCoordinate.removeAt(ind)
                        break
                    }
                }
                it.remove()
                dialog.cancel()
            }
            alert.create().show()
            false
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
    }
}
