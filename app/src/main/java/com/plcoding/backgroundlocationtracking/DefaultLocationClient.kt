package com.plcoding.backgroundlocationtracking

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.activity.ComponentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient?,
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }

//            val locationManager =
//                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            val isNetworkEnabled =
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//            if (!isGpsEnabled && !isNetworkEnabled) {
//                throw LocationClient.LocationException("GPS is disabled")
//            }

            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            client?.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client?.removeLocationUpdates(locationCallback)
            }
        }
    }

    override fun checkGpsOpened(activity : ComponentActivity): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val request = LocationRequest.create()
            .setInterval(50)
            .setFastestInterval(100)

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)

        val task: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Intent(activity, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                activity.startService(this)
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        activity,
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
        return (!isGpsEnabled && !isNetworkEnabled)
    }
}