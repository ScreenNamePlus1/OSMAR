package com.yourcompany.arnav.voice

import java.util.Locale

class VoiceCommandProcessor {
    
    private val commandPatterns = listOf(
        CommandPattern(
            listOf("navigate to", "take me to", "directions to", "how do i get to", "go to"),
            { text -> extractDestination(text)?.let { NavigationCommand.NavigateTo(it) } }
        ),
        CommandPattern(
            listOf("find", "where is", "nearest", "closest"),
            { text -> extractPOIType(text)?.let { NavigationCommand.FindPOI(it) } }
        ),
        CommandPattern(
            listOf("stop navigation", "cancel navigation", "end navigation", "stop directions"),
            { NavigationCommand.StopNavigation }
        ),
        CommandPattern(
            listOf("reroute", "alternative route", "different way", "avoid traffic"),
            { NavigationCommand.Reroute }
        ),
        CommandPattern(
            listOf("pause", "shut up", "be quiet"),
            { NavigationCommand.Pause }
        ),
        CommandPattern(
            listOf("resume", "continue", "start again"),
            { NavigationCommand.Resume }
        ),
        CommandPattern(
            listOf("mute", "turn off voice", "silent mode"),
            { NavigationCommand.Mute }
        ),
        CommandPattern(
            listOf("unmute", "turn on voice", "voice on"),
            { NavigationCommand.Unmute }
        ),
        CommandPattern(
            listOf("where am i", "what's my location", "current location", "my position"),
            { NavigationCommand.WhereAmI }
        ),
        CommandPattern(
            listOf("how far", "remaining distance", "how much further", "when will i arrive", "eta"),
            { NavigationCommand.TimeRemaining }
        ),
        CommandPattern(
            listOf("next turn", "what do i do next", "upcoming turn", "what's next"),
            { NavigationCommand.NextTurn }
        ),
        CommandPattern(
            listOf("speed limit", "what's the speed limit", "how fast can i go"),
            { NavigationCommand.SpeedLimit }
        ),
        CommandPattern(
            listOf("ar mode", "augmented reality", "show camera", "camera view"),
            { NavigationCommand.SwitchToAR }
        ),
        CommandPattern(
            listOf("map mode", "show map", "2d mode", "map view"),
            { NavigationCommand.SwitchToMap }
        ),
        CommandPattern(
            listOf("zoom in", "closer", "magnify"),
            { NavigationCommand.ZoomIn }
        ),
        CommandPattern(
            listOf("zoom out", "further", "see more"),
            { NavigationCommand.ZoomOut }
        ),
        CommandPattern(
            listOf("call emergency", "call 911", "call police", "sos"),
            { NavigationCommand.CallEmergency }
        ),
        CommandPattern(
            listOf("share location", "send location", "share my position"),
            { NavigationCommand.ShareLocation }
        )
    )
    
    private data class CommandPattern(
        val triggers: List<String>,
        val parser: (String) -> NavigationCommand?
    )
    
    fun parseCommand(text: String): NavigationCommand {
        val lower = text.lowercase(Locale.getDefault()).trim()
        
        for (pattern in commandPatterns) {
            for (trigger in pattern.triggers) {
                if (lower.contains(trigger)) {
                    return pattern.parser(lower) ?: NavigationCommand.Unknown
                }
            }
        }
        
        return NavigationCommand.Unknown
    }
    
    private fun extractDestination(text: String): String? {
        val patterns = listOf("navigate to", "take me to", "directions to", "how do i get to", "go to")
        
        for (pattern in patterns) {
            val index = text.indexOf(pattern)
            if (index != -1) {
                return text.substring(index + pattern.length).trim()
                    .removePrefix("the")
                    .removePrefix("a")
                    .removePrefix("an")
                    .trim()
            }
        }
        return null
    }
    
    private fun extractPOIType(text: String): String? {
        val lower = text.lowercase()
        
        return when {
            lower.contains("gas") || lower.contains("petrol") || lower.contains("fuel") || lower.contains("gasoline") -> "fuel"
            lower.contains("restaurant") || lower.contains("food") || lower.contains("eat") || lower.contains("hungry") -> "restaurant"
            lower.contains("coffee") || lower.contains("starbucks") || lower.contains("cafe") || lower.contains("dunkin") -> "cafe"
            lower.contains("hospital") || lower.contains("emergency") || lower.contains("medical") || lower.contains("doctor") -> "hospital"
            lower.contains("parking") || lower.contains("park") -> "parking"
            lower.contains("hotel") || lower.contains("motel") || lower.contains("sleep") -> "hotel"
            lower.contains("atm") || lower.contains("cash") || lower.contains("money") || lower.contains("bank") -> "atm"
            lower.contains("bathroom") || lower.contains("restroom") || lower.contains("toilet") || lower.contains("washroom") -> "toilet"
            lower.contains("pharmacy") || lower.contains("drugstore") || lower.contains("medicine") || lower.contains("pills") -> "pharmacy"
            lower.contains("supermarket") || lower.contains("grocery") || lower.contains("store") || lower.contains("shop") -> "supermarket"
            else -> null
        }
    }
}
