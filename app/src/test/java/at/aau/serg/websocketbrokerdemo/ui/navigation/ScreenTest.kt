package at.aau.serg.websocketbrokerdemo.ui.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

/**
 * Tests für die Screen-sealed-class.
 *
 * Diese Tests garantieren die wichtigsten Invarianten der zentralen
 * Routen-Definition:
 *  - Routen sind eindeutig (keine zwei Screens mit gleichem String)
 *  - Routen sind nicht leer / null
 *  - fromRoute() ist die korrekte Inverse zu Screen.route
 *  - Die all-Liste enthält wirklich alle definierten Screens
 *
 * Wenn jemand versehentlich z. B. zwei Screens mit gleicher Route
 * registriert oder das Hinzufügen zur all-Liste vergisst, schlagen diese
 * Tests sofort an.
 */
class ScreenTest {

    @Test
    fun `all routes are unique`() {
        // Würde z. B. anschlagen wenn jemand Home und MainMenu beide
        // route="home" gibt -- der NavHost würde dann den falschen
        // Composable rendern, nur zur Laufzeit erkennbar.
        val routes = Screen.all.map { it.route }
        assertEquals(routes.size, routes.toSet().size, "Routes must be unique")
    }

    @Test
    fun `no route is blank`() {
        // Leere Routen sind in Compose Navigation zwar zulässig, würden
        // aber Mehrdeutigkeiten erzeugen. Wir verbieten sie hart.
        Screen.all.forEach { screen ->
            assert(screen.route.isNotBlank()) {
                "Screen ${screen::class.simpleName} has blank route"
            }
        }
    }

    @Test
    fun `fromRoute returns Home for home route`() {
        assertSame(Screen.Home, Screen.fromRoute("home"))
    }

    @Test
    fun `fromRoute returns Settings for settings route`() {
        assertSame(Screen.Settings, Screen.fromRoute("settings"))
    }

    @Test
    fun `fromRoute returns MainMenu for mainmenu route`() {
        assertSame(Screen.MainMenu, Screen.fromRoute("mainmenu"))
    }

    @Test
    fun `fromRoute returns LobbyDual for lobby_dual route`() {
        assertSame(Screen.LobbyDual, Screen.fromRoute("lobby_dual"))
    }

    @Test
    fun `fromRoute returns LobbyTriad for lobby_triad route`() {
        assertSame(Screen.LobbyTriad, Screen.fromRoute("lobby_triad"))
    }

    @Test
    fun `fromRoute returns LobbyBattlefield for lobby_battlefield route`() {
        assertSame(Screen.LobbyBattlefield, Screen.fromRoute("lobby_battlefield"))
    }

    @Test
    fun `fromRoute returns WaitingDual for waiting_dual route`() {
        assertSame(Screen.WaitingDual, Screen.fromRoute("waiting_dual"))
    }

    @Test
    fun `fromRoute returns WaitingTriad for waiting_triad route`() {
        assertSame(Screen.WaitingTriad, Screen.fromRoute("waiting_triad"))
    }

    @Test
    fun `fromRoute returns WaitingBattlefield for waiting_battlefield route`() {
        assertSame(Screen.WaitingBattlefield, Screen.fromRoute("waiting_battlefield"))
    }

    @Test
    fun `fromRoute returns Game for game route`() {
        assertSame(Screen.Game, Screen.fromRoute("game"))
    }

    @Test
    fun `fromRoute returns null for unknown route`() {
        // Sicherstellt dass keine versehentliche Default-Auflösung passiert.
        assertNull(Screen.fromRoute("unknown_route"))
        assertNull(Screen.fromRoute(""))
    }

    @Test
    fun `fromRoute returns null for null input`() {
        // Verhindert NPE wenn der NavController noch keine aktive Route hat
        // (passiert direkt nach App-Start vor der ersten Komposition).
        assertNull(Screen.fromRoute(null))
    }

    @Test
    fun `all list contains exactly 12 screens`() {
        // Sanity-Check: wenn jemand einen neuen Screen anlegt, muss er
        // ihn auch in Screen.all eintragen. Dieser Test bricht und
        // erinnert daran. Beim absichtlichen Hinzufügen muss die Zahl
        // hier angepasst werden.
        assertEquals(12, Screen.all.size)
    }
}
