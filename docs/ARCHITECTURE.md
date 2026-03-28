# ARNav Architecture

## System Overview

arnav/ ├── app/ │ ├── ar/ │ ├── navigation/ │ ├── voice/ │ ├── map/ │ └── data/ └── docs/


## Module Descriptions

### 1. Voice Layer (voice/)
- VoskRecognizer: Real-time speech-to-text using Vosk
- VoiceCommandProcessor: NLP parsing for navigation commands
- WakeWordDetector: "Hey Navigator" activation
- NavigationCommands: Sealed class of all supported commands

### 2. AR Layer (ar/)
- ARSession: Manages OpenCV SLAM session
- ARRenderer: OpenGL ES rendering of 3D navigation elements
- ARRouteVisualizer: Converts route to AR anchors and arrows
- OpenCVSLAM: Visual odometry and spatial tracking

### 3. Navigation Layer (navigation/)
- RoutingEngine: GraphHopper wrapper for offline routing
- RouteManager: Active route state and progress tracking
- TurnInstructions: Turn-by-turn guidance generation
- NavigationAlerts: Speed limits, cameras, hazards

### 4. Map Layer (map/)
- MapManager: MapLibre integration
- MapStyleManager: OSM style configuration

### 5. Data Layer (data/)
- OSMDownloadManager: Download and update map regions
- GraphHopperStore: Local routing graph storage
- POIRepository: Points of interest search

## Data Flow

1. Voice Command -> Vosk STT -> Command Parser -> Action
2. Route Request -> GraphHopper -> Route Object -> AR + Map Display
3. Location Update -> SLAM Tracking -> AR Anchor Update
4. Alert Trigger -> NavigationAlerts -> Voice TTS + AR Visual

## Technology Stack

| Component | Technology | License |
|-----------|-----------|---------|
| Routing | GraphHopper 11.0 | Apache 2.0 |
| Maps | MapLibre Native | BSD-3-Clause |
| AR | OpenCV 4.9 + Custom SLAM | Apache 2.0 |
| Voice | Vosk | Apache 2.0 |
| Language | Kotlin 2.1 | - |
| Build | Gradle 8.7 | - |
