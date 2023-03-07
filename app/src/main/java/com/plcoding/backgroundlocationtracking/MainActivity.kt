package com.plcoding.backgroundlocationtracking

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.plcoding.backgroundlocationtracking.ui.theme.BackgroundLocationTrackingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )

        setContent {
            BackgroundLocationTrackingTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
                        DefaultLocationClient(this@MainActivity, null).checkGpsOpened(this@MainActivity)
                    }) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            startService(this)
                        }
                    }) {
                        Text(text = "Stop")
                    }
                }
            }
        }
    }
}
