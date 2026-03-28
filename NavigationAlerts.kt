package com.yourcompany.arnav.navigation

class NavigationAlerts {
    
    companion object {
        const val SPEED_LIMIT_INITIAL_DELAY_MS = 5000L
        const val SPEED_LIMIT_REPEAT_DELAY_MS = 120000L
        const val SPEED_LIMIT_RESET_DELAY_MS = 30000L
        const val APPROACHING_DISTANCE_M = 150.0
        const val PASSING_DISTANCE_M = 50.0
        const val PASSED_DISTANCE_M = -25.0
    }
    
    private val speedLimitMonitor = SpeedLimitMonitor()
    private val proximityAlerts = mutableListOf<ProximityAlert>()
    
    fun checkSpeedLimit(
        currentSpeedKmh: Float,
        speedLimitKmh: Int,
        toleranceKmh: Int = 5
    ): AlertInfo? {
        return speedLimitMonitor.check(currentSpeedKmh, speedLimitKmh, toleranceKmh)
    }
    
    fun checkProximityAlerts(
        currentLocation: GeoPoint,
        alerts: List<AlertPoint>
    ): List<AlertInfo> {
        val triggered = mutableListOf<AlertInfo>()
        
        alerts.forEach { alert ->
            val distance = currentLocation.distanceTo(alert.location)
            
            when {
                distance <= APPROACHING_DISTANCE_M && distance > PASSING_DISTANCE_M -> {
                    triggered.add(
                        AlertInfo(
                            type = alert.type,
                            location = alert.location,
                            distance = distance,
                            severity = AlertSeverity.WARNING,
                            message = "${alert.type.description} in ${distance.toInt()} meters"
                        )
                    )
                }
                distance <= PASSING_DISTANCE_M && distance >= PASSED_DISTANCE_M -> {
                    triggered.add(
                        AlertInfo(
                            type = alert.type,
                            location = alert.location,
                            distance = 0.0,
                            severity = AlertSeverity.CRITICAL,
                            message = "${alert.type.description} now!"
                        )
                    )
                }
            }
        }
        
        return triggered
    }
    
    fun registerAlert(alert: ProximityAlert) {
        proximityAlerts.add(alert)
    }
    
    fun clearAlerts() {
        proximityAlerts.clear()
        speedLimitMonitor.reset()
    }
}

class SpeedLimitMonitor {
    private var lastWarningTime: Long = 0
    private var withinLimitSince: Long = 0
    private var isExceeding = false
    
    fun check(
        currentSpeed: Float,
        speedLimit: Int,
        tolerance: Int
    ): AlertInfo? {
        val now = System.currentTimeMillis()
        val effectiveLimit = speedLimit + tolerance
        
        return when {
            currentSpeed > effectiveLimit -> {
                handleExceeding(now, currentSpeed, speedLimit)
            }
            isExceeding -> {
                handleRecovering(now)
                null
            }
            else -> null
        }
    }
    
    private fun handleExceeding(now: Long, speed: Float, limit: Int): AlertInfo? {
        withinLimitSince = 0
        
        return if (!isExceeding) {
            if (now - lastWarningTime > NavigationAlerts.SPEED_LIMIT_INITIAL_DELAY_MS) {
                isExceeding = true
                lastWarningTime = now
                AlertInfo(
                    type = AlertType.SPEED_LIMIT,
                    location = GeoPoint(0.0, 0.0),
                    distance = 0.0,
                    severity = AlertSeverity.WARNING,
                    message = "Speed limit ${limit} km/h exceeded",
                    extraData = mapOf("currentSpeed" to speed, "limit" to limit)
                )
            } else null
        } else {
            if (now - lastWarningTime > NavigationAlerts.SPEED_LIMIT_REPEAT_DELAY_MS) {
                lastWarningTime = now
                AlertInfo(
                    type = AlertType.SPEED_LIMIT,
                    location = GeoPoint(0.0, 0.0),
                    distance = 0.0,
                    severity = AlertSeverity.WARNING,
                    message = "You are still exceeding the speed limit",
                    extraData = mapOf("currentSpeed" to speed, "limit" to limit)
                )
            } else null
        }
    }
    
    private fun handleRecovering(now: Long) {
        if (withinLimitSince == 0L) {
            withinLimitSince = now
        } else if (now - withinLimitSince > NavigationAlerts.SPEED_LIMIT_RESET_DELAY_MS) {
            isExceeding = false
            lastWarningTime = 0
        }
    }
    
    fun reset() {
        lastWarningTime = 0
        withinLimitSince = 0
        isExceeding = false
    }
}

data class AlertInfo(
    val type: AlertType,
    val location: GeoPoint,
    val distance: Double,
    val severity: AlertSeverity,
    val message: String,
    val extraData: Map<String, Any> = emptyMap()
)

data class AlertPoint(
    val type: AlertType,
    val location: GeoPoint,
    val metadata: Map<String, Any> = emptyMap()
)

data class ProximityAlert(
    val point: AlertPoint,
    val triggerDistance: Double = NavigationAlerts.APPROACHING_DISTANCE_M
)

enum class AlertType(val description: String) {
    SPEED_LIMIT("Speed limit"),
    SPEED_CAMERA("Speed camera"),
    RED_LIGHT_CAMERA("Red light camera"),
    STOP_SIGN("Stop sign"),
    RAILROAD_CROSSING("Railroad crossing"),
    PEDESTRIAN_CROSSING("Pedestrian crossing"),
    TUNNEL("Tunnel"),
    TOLL_BOOTH("Toll booth"),
    BORDER_CONTROL("Border control"),
    TRAFFIC_CALMING("Traffic calming"),
    HAZARD("Hazard ahead")
}

enum class AlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}
