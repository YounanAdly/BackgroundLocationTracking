package com.plcoding.backgroundlocationtracking

import android.location.Location
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>
    fun checkGpsOpened(activity : ComponentActivity) :Boolean

    class LocationException(message: String): Exception()
}