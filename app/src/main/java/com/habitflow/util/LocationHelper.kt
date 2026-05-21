package com.habitflow.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context): Pair<Double, Double>? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location =
            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        return location?.let { it.latitude to it.longitude }
    }
}
