# ARNav Setup Guide

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- NDK 25.1.8937393
- Kotlin 2.1.10
- Git

## Initial Setup

### 1. Clone Repository

git clone https://github.com/yourusername/arnav.git
cd arnav

### 2. Build Project

./gradlew assembleDebug

### 3. Install on Device

adb install app/build/outputs/apk/debug/app-debug.apk

## Development Environment

### Required Environment Variables

export ANDROID_SDK=$HOME/Android/Sdk
export ANDROID_NDK=$HOME/Android/Sdk/ndk/25.1.8937393

## Vosk Model Setup

Models are downloaded automatically on first app launch, or manually:

mkdir -p app/src/main/assets/models

wget https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
unzip vosk-model-small-en-us-0.15.zip -d app/src/main/assets/models/
mv app/src/main/assets/models/vosk-model-small-en-us-0.15 app/src/main/assets/models/vosk-model

