// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.sonarqube") version "7.3.0.8198"
}

sonar {
    properties {
        property("sonar.projectKey", "HexaBrawl_app")
        property("sonar.organization", "hexabrawl")
        property("sonar.host.url", "https://sonarcloud.io")

        // Coverage-Exclusions:
        //
        // Ausgeschlossen werden alle Files, die nicht sinnvoll im
        // Unit-Test-Scope getestet werden können (Variante B = ohne Robolectric):
        //
        //  - Composables (UI-Screens): brauchen androidx.compose.ui.test
        //  - Canvas-Drawing (Grid-Renderer): testet nur Pixel-Output
        //  - Activity / Network (MainActivity, Stomp): Lifecycle/Sockets
        //  - AndroidViewModel: braucht echtes Application-Object
        //  - Theme-Konstanten: reine Farb-/Font-Definitionen
        property(
            "sonar.coverage.exclusions",
            listOf(
                // Composables / UI-Screens
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/components/**",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/game/GameScreen.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/home/HomeScreen.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/lobby_modes/ActionCard.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/lobby_modes/JoinByCodeDialog.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/lobby_modes/LobbyScreen.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/lobby_modes/RoundCoinButton.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/mainmenu/MainMenuScreen.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/navigation/AppNavHost.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/settings/SettingsScreen.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/theme/**",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/waiting/WaitingLobbyComponents.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/waiting/WaitingLobbyScreen.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/waiting/WaitingLobbyState.kt",

                // Grid-Rendering (Canvas)
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/grid/UniversalGrid.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/grid/renderer/**",

                // Lifecycle / Network
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/MainActivity.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/network/Stomp.kt",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/network/GameSession.kt",

                // AndroidViewModel (braucht Robolectric)
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/settings/SettingsViewModel.kt"
            ).joinToString(",")
        )

        // Aus der Code-Duplication-Analyse: Composables und Grid-Renderer
        // haben strukturell ähnliche Boilerplate (Brushes, Modifiers, etc.),
        // das gäbe False-Positives.
        property(
            "sonar.cpd.exclusions",
            listOf(
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/ui/**",
                "app/src/main/java/at/aau/serg/websocketbrokerdemo/grid/renderer/**"
            ).joinToString(",")
        )
    }
}