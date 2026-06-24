package at.aau.serg.websocketbrokerdemo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Zentrale Farb-Palette des HexaBrawl-Themes.
 *
 * Die Farben sind in vier semantische Gruppen geordnet -- Pergament
 * (Hintergruende, Map, Cards), Tinte (Text und Linien), Holz (Buttons und
 * Rahmen) und Metall (Muenzen und Akzente). Alle Composables beziehen ihre
 * Farben hierueber, damit das mittelalterliche Pergament-Look konsistent
 * bleibt und Aenderungen an einer Stelle passieren.
 */

// Pergament-Töne (Hintergrund, Map, Cards)
val ParchmentLight = Color(0xFFF5E6C4)   // helles, sauberes Pergament
val ParchmentBase  = Color(0xFFE8D5A8)   // Standard-Pergament
val ParchmentDark  = Color(0xFFC9B17A)   // gealtert, Schattenkanten
val ParchmentEdge  = Color(0xFF8B6F3D)   // dunkle, abgenutzte Kante

// Tinte (Text, Linien, Hexagon-Grenzen)
val InkBlack       = Color(0xFF2B1D0E)   // Federzeichnung-Tinte
val InkBrown       = Color(0xFF5C3A1A)   // Sekundärtext, ältere Tinte
val InkFaded       = Color(0xFF7A5C36)   // verblasste Schrift

// Holz (Buttons, Rahmen)
val WoodDark       = Color(0xFF4A2E1A)
val WoodMedium     = Color(0xFF6B4226)
val WoodLight      = Color(0xFF8B5A2B)

// Metall (Münzen, Akzente)
val GoldCoin       = Color(0xFFD4A24C)   // Hauptgebäude / Hervorhebung
val GoldCoinLight  = Color(0xFFE9C170)
val GoldCoinDark   = Color(0xFF9C6F22)
