// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    //noinspection NewerVersionAvailable - Do not mark this as a warning
    id("org.sonarqube") version "5.1.0.4882"
}

sonar {
    properties{
        property("sonar.projectKey","HexaBrawl_app")
        property("sonar.organization","hexabrawl")
        property("sonar.host.url", "https://sonarcloud.io")
        //Exclude UI folders from testing
        property("sonar.coverage.exclusions", "**/at/aau/serg/websocketbrokerdemo/ui/**")
        property("sonar.exclusions", "**/at/aau/serg/websocketbrokerdemo/grid/HexGridOld.kt")
    }
}        