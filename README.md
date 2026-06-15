# 📱 HexaBrawl - Android Client

Die mobile Anwendung für das Hexagon-Strategieduell. Diese App wird mit **Kotlin** und **Jetpack Compose** entwickelt und kommuniziert in Echtzeit mit dem Game-Server.


## 🚀 Quick Start
1. Repository klonen.
2. Das Projekt in  **Android Studio** öffnen.
3. Abwarten, bis der Gradle-Sync abgeschlossen ist.
4. Auf einem Emulator oder physischen Gerät ausführen.

## 🛠️ Verwendete Technologien
* **UI-Framework:** Jetpack Compose (Modernes, deklaratives UI)
* **Sprache:** Kotlin
* **Netzwerk:** STOMP-Protokoll für WebSockets

## 📡 Kommunikation
Die App spricht den Game-Server über zwei TLS-Kanäle an (produktiv auf Azure gehostet):

* **REST** (`RoomApiClient`): Raum erstellen / per Code beitreten — Basis-URL
  `https://hexabrawl-server-…azurewebsites.net`
* **STOMP über WebSocket** (`Stomp`): Live-Spielzustand —
  `wss://hexabrawl-server-…azurewebsites.net/websocket-example-broker`

Der Server broadcastet den vollständigen `GameState` pro Raum auf
`/topic/rooms/{roomId}/state`; Aktionen (join, move, end-turn, buy-unit, …)
laufen über `/app/rooms/{roomId}/…`. Die konkreten Endpoint-URLs stehen
zentral in `Stomp.kt` bzw. `RoomApiClient.kt`.

## 🧱 Architektur
* **UI:** schlanke Jetpack-Compose-Screens — die Spiel-/Lobby-Logik liegt in
  reinen, testbaren `*Logic`-Objekten und ViewModels (z. B. `GameScreenLogic`,
  `WaitingLobbyLogic`, `FieldConnectivity`).
* **Single Source of Truth:** der Server-`GameState`; die UI leitet ihren
  Zustand ausschließlich daraus ab.

## 🧪 Build & Tests
* Unit-Tests (JUnit 5 + MockK): `./gradlew :app:testDebugUnitTest`
* Coverage-Report (JaCoCo): `./gradlew :app:jacocoTestReport`
* Statische Analyse + Coverage laufen in CI über **SonarCloud**
  (`.github/workflows/build.yml`).

## ⌯⌲ Deployment
Mit Doco-CD am Uni Server 
Microsoft AzureApp Service mit einem Linux Container

## Backend Repo
https://github.com/HexaBrawl/server
