package com.yourcompany.arnav.ar

import android.content.Context
import com.yourcompany.arnav.navigation.GeoPoint
import org.opencv.core.Mat

class ARSession(private val context: Context) {
    
    private var isInitialized = false
    private val slamProcessor = OpenCVSLAM()
    private val anchors = mutableListOf<ARAnchor>()
    
    private var currentPose: Pose? = null
    private var referenceLocation: GeoPoint? = null
    
    fun initialize(initialLocation: GeoPoint): Boolean {
        referenceLocation = initialLocation
        isInitialized = slamProcessor.initialize()
        return isInitialized
    }
    
    fun processFrame(frame: Mat): Pose? {
        if (!isInitialized) return null
        
        currentPose = slamProcessor.processFrame(frame)
        updateAnchors()
        
        return currentPose
    }
    
    fun createAnchor(
        location: GeoPoint,
        altitude: Double = 0.0
    ): ARAnchor? {
        val ref = referenceLocation ?: return null
        val pose = currentPose ?: return null
        
        val localPos = geoToLocal(ref, location, altitude)
        
        val anchor = ARAnchor(
            id = generateAnchorId(),
            geoLocation = location,
            localPosition = localPos,
            createdAtPose = pose.copy()
        )
        
        anchors.add(anchor)
        return anchor
    }
    
    fun displayRoute(
        path: List<GeoPoint>,
        arrowSpacingMeters: Double = 10.0
    ): List<ARAnchor> {
        clearAnchors()
        
        val routeAnchors = mutableListOf<ARAnchor>()
        
        path.windowed(2, 1).forEach { (current, next) ->
            val distance = current.distanceTo(next)
            
            if (distance > arrowSpacingMeters) {
                val steps = (distance / arrowSpacingMeters).toInt()
                for (i in 0..steps) {
                    val ratio = i / steps.toDouble()
                    val lat = current.latitude + (next.latitude - current.latitude) * ratio
                    val lon = current.longitude + (next.longitude - current.longitude) * ratio
                    val point = GeoPoint(lat, lon)
                    
                    createAnchor(point)?.let { anchor ->
                        anchor.type = AnchorType.ROUTE_POINT
                        routeAnchors.add(anchor)
                    }
                }
            } else {
                createAnchor(current)?.let { anchor ->
                    anchor.type = AnchorType.ROUTE_POINT
                    routeAnchors.add(anchor)
                }
            }
        }
        
        path.lastOrNull()?.let { dest ->
            createAnchor(dest, altitude = 2.0)?.let { anchor ->
                anchor.type = AnchorType.DESTINATION
                routeAnchors.add(anchor)
            }
        }
        
        return routeAnchors
    }
    
    fun showPOIArrow(poi: GeoPoint): ARAnchor? {
        return createAnchor(poi, altitude = 3.0)?.apply {
            type = AnchorType.POI
        }
    }
    
    fun clearAnchors() {
        anchors.clear()
    }
    
    private fun updateAnchors() {
        val pose = currentPose ?: return
        
        anchors.forEach { anchor ->
            anchor.updateScreenPosition(pose)
        }
    }
    
    private fun geoToLocal(
        reference: GeoPoint,
        target: GeoPoint,
        altitude: Double
    ): Vector3 {
        val earthRadius = 6371000.0
        
        val dLat = Math.toRadians(target.latitude - reference.latitude)
        val dLon = Math.toRadians(target.longitude - reference.longitude)
        
        val avgLat = Math.toRadians((reference.latitude + target.latitude) / 2)
        
        val x = earthRadius * dLon * Math.cos(avgLat)
        val z = earthRadius * dLat
        val y = altitude
        
        return Vector3(x, y, z)
    }
    
    private fun generateAnchorId(): String {
        return "anchor_${System.currentTimeMillis()}_${anchors.size}"
    }
    
    fun release() {
        isInitialized = false
        clearAnchors()
        slamProcessor.release()
    }
}

data class ARAnchor(
    val id: String,
    val geoLocation: GeoPoint,
    val localPosition: Vector3,
    val createdAtPose: Pose
) {
    var type: AnchorType = AnchorType.GENERIC
    var screenPosition: Vector2? = null
    var isVisible: Boolean = false
    
    fun updateScreenPosition(currentPose: Pose) {
        // Transform local position to screen coordinates
    }
}

enum class AnchorType {
    GENERIC,
    ROUTE_POINT,
    TURN_INDICATOR,
    DESTINATION,
    POI
}

data class Vector3(val x: Double, val y: Double, val z: Double)
data class Vector2(val x: Float, val y: Float)

data class Pose(
    val translation: Vector3,
    val rotation: FloatArray
) {
    fun copy(): Pose {
        return Pose(translation, rotation.copyOf())
    }
}
