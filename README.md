# ARNav - Augmented Reality Navigation

A standalone AR navigation app with offline routing, voice control, and 3D visual guidance.

## Features

- Offline Navigation: GraphHopper-based routing with OpenStreetMap data
- AR Visualization: 3D navigation arrows using OpenCV SLAM
- Voice Control: Vosk-based speech recognition for hands-free operation
- Privacy-First: No Google services, no server dependencies, fully offline capable

## Architecture

- Map Rendering: MapLibre GL (BSD License)
- Routing Engine: GraphHopper (Apache 2.0)
- AR Framework: OpenCV + Custom SLAM (Apache 2.0)
- Voice Recognition: Vosk (Apache 2.0)
- App License: Proprietary (Commercial)

## Project Structure

arnav/ ├── app/ # Main Android application │ ├── ar/ # AR visualization layer │ ├── navigation/ # Routing and turn-by-turn logic │ ├── voice/ # Vosk integration and command processing │ ├── map/ # 2D map management │ └── data/ # OSM data and GraphHopper storage └── docs/ # Documentation


## Setup

See SETUP.md for detailed installation instructions.

## License

This project is proprietary software. See LICENSE for details.

Third-party components:
- GraphHopper: Apache 2.0
- MapLibre: BSD-3-Clause
- OpenCV: Apache 2.0
- Vosk: Apache 2.0
