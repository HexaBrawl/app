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


## 🎨 UI/UX & Design (In Arbeit)
Die ersten Mockups und Screens für die Lobby und das Spielfeld werden aktuell erstellt. Details zum Design-Konzept findest du im Wiki unter "UI-Skizzen".


## 📡 Kommunikation
Die App verbindet sich beim Start mit dem Game-Server via WebSockets. 
Standard-Endpoint für lokale Entwicklung: `ws://10.0.2.2:8080/ws` (Android Emulator Adresse für localhost).

## Link zum Wiki
https://github.com/HexaBrawl/server/wiki

## Backend Repo
https://github.com/HexaBrawl/server

