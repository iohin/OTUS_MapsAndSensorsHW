package com.sample.otuslocationmapshw

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sample.otuslocationmapshw.camera.CameraActivity
import com.sample.otuslocationmapshw.data.utils.LocationDataUtils
import com.sample.otuslocationmapshw.databinding.ActivityMapsBinding
import java.io.File

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val locationDataUtils = LocationDataUtils()
    private val cameraForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.SUCCESS_RESULT_CODE) {
            // TODO("Обновить точки на карте при получении результата от камеры")
            showPreviewsOnMap()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                cameraForResultLauncher.launch(Intent(this, CameraActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        showPreviewsOnMap()
    }

    private fun showPreviewsOnMap() {
        map.clear()
        val folder = File("${filesDir.absolutePath}/photos/")
        folder.listFiles()?.forEach {
            val exifInterface = ExifInterface(it)
            val location = locationDataUtils.getLocationFromExif(exifInterface)
            val point = LatLng(location.latitude, location.longitude)
            val pinBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeFile(
                    it.path,
                    BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }), 64, 64, false
            )
            // TODO("Указать pinBitmap как иконку для маркера")
            map.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(pinBitmap))
                    .position(point)
            )
            // TODO("Передвинуть карту к местоположению последнего фото")
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude,
                    ),
                    12f
                )
            )
        }
    }
}