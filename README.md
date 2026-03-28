arnav-project/
├── README.md                           # Project overview
├── LICENSE                             # Your proprietary license
├── build.gradle.kts                    # Root build config
├── settings.gradle.kts                 # Project settings
├── gradle/
│   └── libs.versions.toml              # Version catalog
├── app/
│   ├── build.gradle.kts                # App module build
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/yourcompany/arnav/
│       │   ├── MainActivity.kt
│       │   ├── ARNavigationActivity.kt
│       │   ├── di/
│       │   │   └── AppModule.kt
│       │   ├── ar/
│       │   │   ├── ARSession.kt
│       │   │   ├── ARRenderer.kt
│       │   │   ├── ARRouteVisualizer.kt
│       │   │   └── OpenCVSLAM.kt
│       │   ├── navigation/
│       │   │   ├── RoutingEngine.kt
│       │   │   ├── RouteManager.kt
│       │   │   ├── TurnInstructions.kt
│       │   │   └── NavigationAlerts.kt
│       │   ├── voice/
│       │   │   ├── WhisperRecognizer.kt
│       │   │   ├── VoiceCommandProcessor.kt
│       │   │   ├── NavigationCommands.kt
│       │   │   └── WakeWordDetector.kt
│       │   ├── map/
│       │   │   ├── MapManager.kt
│       │   │   └── MapStyleManager.kt
│       │   ├── data/
│       │   │   ├── OSMDownloadManager.kt
│       │   │   ├── GraphHopperStore.kt
│       │   │   └── POIRepository.kt
│       │   └── utils/
│       │       ├── GeoUtils.kt
│       │       └── PermissionsManager.kt
│       └── res/
│           ├── layout/
│           ├── values/
│           └── raw/
└── docs/
    ├── ARCHITECTURE.md
    ├── SETUP.md
    └── API.md
