package com.yourcompany.arnav.navigation

import android.content.Context
import com.graphhopper.GraphHopper
import com.graphhopper.config.Profile
import com.graphhopper.routing.Request
import com.graphhopper.util.PointList
import com.graphhopper.util.shapes.GHPoint
import java.io.File

class RoutingEngine(private val context: Context) {
    
    private var hopper: GraphHopper? = null
    private val graphDir = File(context.filesDir, "graphhopper/graphs")
    
    companion object {
        const val PROFILE_CAR = "car"
        const val PROFILE_FOOT = "foot"
        const val PROFILE_BIKE = "bike"
    }
    
    fun initialize(): Boolean {
        return try {
            graphDir.mkdirs()
            
            hopper = GraphHopper().apply {
                setGraphHopperLocation(graphDir.absolutePath)
                setProfiles(
                    Profile(PROFILE_CAR).apply {
                        vehicle = "car"
                        weighting = "fastest"
                    },
                    Profile(PROFILE_FOOT).apply {
                        vehicle = "foot"
                        weighting = "shortest"
                    },
                    Profile(PROFILE_BIKE).apply {
                        vehicle = "bike"
                        weighting = "fastest"
                    }
                )
                
                if (!importOrLoad()) {
                    return false
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun calculateRoute(
        from: GeoPoint,
        to: GeoPoint,
        profile: String = PROFILE_CAR
    ): RouteResult? {
        val gh = hopper ?: return null
        
        return try {
            val req = Request(
                GHPoint(from.latitude, from.longitude),
                GHPoint(to.latitude, to.longitude)
            ).apply {
                setProfile(profile)
                setLocale(context.resources.configuration.locale)
                setPointsEncoded(false)
            }
            
            val rsp = gh.route(req)
            
            if (rsp.hasErrors()) {
                return null
            }
            
            val path = rsp.best
            RouteResult(
                path = path.points.toGeoPoints(),
                distance = path.distance,
                time = path.time,
                instructions = path.instructions.map { 
                    TurnInstruction(
                        text = it.text,
                        distance = it.distance,
                        time = it.time,
                        sign = it.sign,
                        streetName = it.streetName,
                        exitNumber = it.exitNumber
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun isDataAvailable(): Boolean {
        return hopper != null && graphDir.exists() && graphDir.listFiles()?.isNotEmpty() == true
    }
    
    fun getRouteProgress(
        route: RouteResult,
        currentLocation: GeoPoint
    ): RouteProgress? {
        val closestIndex = findClosestPointIndex(route.path, currentLocation)
        if (closestIndex == -1) return null
        
        val remainingPath = route.path.subList(closestIndex, route.path.size)
        val remainingDistance = calculatePathDistance(remainingPath)
        val remainingTime = estimateTime(remainingDistance)
        
        return RouteProgress(
            remainingDistance = remainingDistance,
            remainingTime = remainingTime,
            nextTurn = findNextTurn(route, closestIndex)
        )
    }
    
    private fun findClosestPointIndex(path: List<GeoPoint>, location: GeoPoint): Int {
        var minDistance = Double.MAX_VALUE
        var minIndex = -1
        
        path.forEachIndexed { index, point ->
            val dist = location.distanceTo(point)
            if (dist < minDistance) {
                minDistance = dist
                minIndex = index
            }
        }
        
        return minIndex
    }
    
    private fun calculatePathDistance(path: List<GeoPoint>): Double {
        var distance = 0.0
        for (i in 0 until path.size - 1) {
            distance += path[i].distanceTo(path[i + 1])
        }
        return distance
    }
    
    private fun estimateTime(distanceMeters: Double, profile: String = PROFILE_CAR): Long {
        val speedKmh = when (profile) {
            PROFILE_CAR -> 50.0
            PROFILE_FOOT -> 5.0
            PROFILE_BIKE -> 15.0
            else -> 50.0
        }
        return ((distanceMeters / 1000) / speedKmh * 3600 * 1000).toLong()
    }
    
    private fun findNextTurn(route: RouteResult, currentIndex: Int): TurnInstruction? {
        return route.instructions.firstOrNull { instruction ->
            instruction.distance > 0
        }
    }
    
    private fun PointList.toGeoPoints(): List<GeoPoint> {
        return (0 until size()).map { i ->
            GeoPoint(getLat(i), getLon(i))
        }
    }
    
    fun shutdown() {
        hopper?.close()
        hopper = null
    }
}

data class RouteResult(
    val path: List<GeoPoint>,
    val distance: Double,
    val time: Long,
    val instructions: List<TurnInstruction>
)

data class TurnInstruction(
    val text: String,
    val distance: Double,
    val time: Long,
    val sign: Int,
    val streetName: String?,
    val exitNumber: Int = -1
)

data class RouteProgress(
    val remainingDistance: Double,
    val remainingTime: Long,
    val nextTurn: TurnInstruction?
)

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null
) {
    fun distanceTo(other: GeoPoint): Double {
        val R = 6371000.0
        val lat1 = Math.toRadians(latitude)
        val lat2 = Math.toRadians(other.latitude)
        val deltaLat = Math.toRadians(other.latitude - latitude)
        val deltaLon = Math.toRadians(other.longitude - longitude)
        
        val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return R * c
    }
}
