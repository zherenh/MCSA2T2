package io.github.sceneview.sample.arcursorplacement

interface LocationCallback {
    fun onLocationResult(latitude: Double, longitude: Double, altitude: Double)
    fun onPermissionDenied()
    fun onLocationUnavailable()
}