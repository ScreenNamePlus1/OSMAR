package com.yourcompany.arnav.voice

sealed class NavigationCommand {
    data class NavigateTo(val destination: String) : NavigationCommand()
    data class FindPOI(val type: String) : NavigationCommand()
    object StopNavigation : NavigationCommand()
    object Reroute : NavigationCommand()
    object Pause : NavigationCommand()
    object Resume : NavigationCommand()
    object Mute : NavigationCommand()
    object Unmute : NavigationCommand()
    object WhereAmI : NavigationCommand()
    object TimeRemaining : NavigationCommand()
    object DistanceRemaining : NavigationCommand()
    object NextTurn : NavigationCommand()
    object SpeedLimit : NavigationCommand()
    object SwitchToAR : NavigationCommand()
    object SwitchToMap : NavigationCommand()
    object ZoomIn : NavigationCommand()
    object ZoomOut : NavigationCommand()
    object CallEmergency : NavigationCommand()
    object ShareLocation : NavigationCommand()
    object Unknown : NavigationCommand()
    
    companion object {
        fun getDescription(command: NavigationCommand): String {
            return when (command) {
                is NavigateTo -> "Navigate to ${command.destination}"
                is FindPOI -> "Find nearest ${command.type}"
                StopNavigation -> "Stop navigation"
                Reroute -> "Reroute"
                Pause -> "Pause guidance"
                Resume -> "Resume guidance"
                Mute -> "Mute voice"
                Unmute -> "Unmute voice"
                WhereAmI -> "Current location"
                TimeRemaining -> "Time to destination"
                DistanceRemaining -> "Distance remaining"
                NextTurn -> "Next turn"
                SpeedLimit -> "Current speed limit"
                SwitchToAR -> "Switch to AR mode"
                SwitchToMap -> "Switch to map mode"
                ZoomIn -> "Zoom in"
                ZoomOut -> "Zoom out"
                CallEmergency -> "Call emergency services"
                ShareLocation -> "Share location"
                Unknown -> "Unknown command"
            }
        }
    }
}
