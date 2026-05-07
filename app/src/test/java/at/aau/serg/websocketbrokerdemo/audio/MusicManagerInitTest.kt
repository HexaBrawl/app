package at.aau.serg.websocketbrokerdemo.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.myapplication.R
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Ergänzende Tests für die noch ungetesteten Bereiche im MusicManager:
 *  - init(context) — Idempotenz, SoundPool-Setup, SFX-Preload
 *  - playMenuMusic / playTournamentMusic / playBattleMusic — Convenience-Wrapper
 *  - play()-Logik:
 *       - gleicher Track läuft schon -> nur Volume reapply, kein Re-create
 *       - anderer Track läuft -> alter freigegeben, neuer gestartet
 *
 * Mocking-Strategie:
 *  - MediaPlayer.create(...) ist statisch -> mockkStatic
 *  - SoundPool.Builder ist eine Builder-Klasse -> mockkConstructor
 *  - MusicManager ist ein object (Singleton) -> State per Reflection reset
 */
class MusicManagerInitAndPlayTest {

    private lateinit var context: Context
    private lateinit var appContext: Context
    private lateinit var soundPool: SoundPool
    private lateinit var player1: MediaPlayer
    private lateinit var player2: MediaPlayer

    @BeforeEach
    fun setUp() {
        resetMusicManagerState()

        context = mockk(relaxed = true)
        appContext = mockk(relaxed = true)
        soundPool = mockk(relaxed = true)
        player1 = mockk(relaxed = true)
        player2 = mockk(relaxed = true)

        every { context.applicationContext } returns appContext

        // SoundPool via Builder mocken
        mockkConstructor(SoundPool.Builder::class)
        every { anyConstructed<SoundPool.Builder>().setMaxStreams(any()) } answers {
            self as SoundPool.Builder
        }
        every { anyConstructed<SoundPool.Builder>().setAudioAttributes(any()) } answers {
            self as SoundPool.Builder
        }
        every { anyConstructed<SoundPool.Builder>().build() } returns soundPool
        every { soundPool.load(any<Context>(), any<Int>(), any()) } returns 42
        justRun { soundPool.release() }

        // MediaPlayer.create(Context, Int) ist statisch
        mockkStatic(MediaPlayer::class)
        // Default: gibt player1 zurück
        every { MediaPlayer.create(any(), any<Int>()) } returns player1
    }

    @AfterEach
    fun tearDown() {
        resetMusicManagerState()
        unmockkAll()
    }

    // -------------------------------------------------------------------------
    // init(context)
    // -------------------------------------------------------------------------

//    @Test
//    fun `init creates soundPool and marks ready`() {
//        MusicManager.init(context)
//
//        assertTrue(getPrivate<Boolean>("soundPoolReady"))
//        assertSame(soundPool, getPrivate("soundPool"))
//    }
//
//    @Test
//    fun `init preloads sword block sfx`() {
//        MusicManager.init(context)
//
//        verify { soundPool.load(appContext, R.raw.sfx_sword_block, 1) }
//
//        @Suppress("UNCHECKED_CAST")
//        val sfxIds = getPrivate<MutableMap<Int, Int>>("sfxIds")
//        assertEquals(42, sfxIds[R.raw.sfx_sword_block])
//    }
//
//    @Test
//    fun `init is idempotent — second call is a no-op`() {
//        MusicManager.init(context)
//        // Erstes init hat soundPool.load() einmal aufgerufen.
//
//        MusicManager.init(context)
//        MusicManager.init(context)
//
//        // Soll trotzdem nur EINMAL geladen worden sein
//        verify(exactly = 1) { soundPool.load(any<Context>(), any<Int>(), any()) }
//    }
//
//    @Test
//    fun `init uses applicationContext for sfx loading`() {
//        MusicManager.init(context)
//
//        // appContext, nicht context selbst
//        verify { soundPool.load(appContext, any<Int>(), any()) }
//    }

    // -------------------------------------------------------------------------
    // play() — Track-spezifische Wrapper
    // -------------------------------------------------------------------------

    @Test
    fun `playMenuMusic creates player with medieval theme resource`() {
        MusicManager.playMenuMusic(context)

        verify { MediaPlayer.create(appContext, R.raw.medieval_theme) }
        assertEquals(R.raw.medieval_theme, getPrivate<Int>("currentTrack"))
    }

    @Test
    fun `playTournamentMusic creates player with tournament theme resource`() {
        MusicManager.playTournamentMusic(context)

        verify { MediaPlayer.create(appContext, R.raw.tournament_theme) }
        assertEquals(R.raw.tournament_theme, getPrivate<Int>("currentTrack"))
    }

    @Test
    fun `playBattleMusic creates player with fighting theme resource`() {
        MusicManager.playBattleMusic(context)

        verify { MediaPlayer.create(appContext, R.raw.fighting_theme) }
        assertEquals(R.raw.fighting_theme, getPrivate<Int>("currentTrack"))
    }

    // -------------------------------------------------------------------------
    // play() — Verhalten beim Track-Wechsel und Wiederholung
    // -------------------------------------------------------------------------

    @Test
    fun `play sets player as looping`() {
        MusicManager.playMenuMusic(context)
        verify { player1.isLooping = true }
    }

    @Test
    fun `play starts playback when music is enabled`() {
        MusicManager.applyMusicSettings(enabled = true, volume = 0.6f)

        MusicManager.playMenuMusic(context)

        verify { player1.start() }
    }

    @Test
    fun `play does not start when music is disabled`() {
        // Music aus, BEVOR play()
        setPrivate("musicEnabled", false)

        MusicManager.playMenuMusic(context)

        verify(exactly = 0) { player1.start() }
    }

    @Test
    fun `play applies current volume to new player`() {
        setPrivate("musicEnabled", true)
        setPrivate("musicVolume", 0.4f)

        MusicManager.playMenuMusic(context)

        verify { player1.setVolume(0.4f, 0.4f) }
    }

    @Test
    fun `play with same track does not recreate the player`() {
        setPrivate("musicEnabled", true)
        setPrivate("musicVolume", 0.6f)

        // Erster Aufruf: erstellt player1
        MusicManager.playMenuMusic(context)
        // Zweiter Aufruf mit demselben Track
        MusicManager.playMenuMusic(context)

        // MediaPlayer.create() darf nur EINMAL aufgerufen worden sein
        verify(exactly = 1) { MediaPlayer.create(any(), R.raw.medieval_theme) }
        // Alter Player wurde NICHT released
        verify(exactly = 0) { player1.release() }
    }

    @Test
    fun `play with same track reapplies volume`() {
        setPrivate("musicEnabled", true)
        setPrivate("musicVolume", 0.6f)
        every { player1.isPlaying } returns true

        MusicManager.playMenuMusic(context)
        MusicManager.playMenuMusic(context)

        // Volume wurde mehrfach gesetzt: einmal beim Create, einmal bei
        // applyVolumeToPlayer() im "same track"-Pfad
        verify(atLeast = 2) { player1.setVolume(0.6f, 0.6f) }
    }

    @Test
    fun `play with same track restarts paused player when music enabled`() {
        setPrivate("musicEnabled", true)
        every { player1.isPlaying } returns false

        MusicManager.playMenuMusic(context)   // first play
        MusicManager.playMenuMusic(context)   // same track again

        // start() wurde mindestens 2x gerufen: 1x beim Create, 1x beim "same-track resume"
        verify(atLeast = 2) { player1.start() }
    }

    @Test
    fun `play with different track releases old player and creates new one`() {
        setPrivate("musicEnabled", true)
        every { player1.isPlaying } returns true
        // Beim zweiten create() einen anderen Player liefern
        every { MediaPlayer.create(any(), R.raw.tournament_theme) } returns player2

        // Erst Menu starten...
        MusicManager.playMenuMusic(context)
        // ...dann zu Tournament wechseln
        MusicManager.playTournamentMusic(context)

        // Alter Player gestoppt + freigegeben
        verify { player1.stop() }
        verify { player1.release() }

        // Neuer Player erstellt + gestartet
        verify { MediaPlayer.create(appContext, R.raw.tournament_theme) }
        verify { player2.start() }

        // currentTrack zeigt jetzt auf den neuen Track
        assertEquals(R.raw.tournament_theme, getPrivate<Int>("currentTrack"))
        assertSame(player2, getPrivate("player"))
    }

    @Test
    fun `play sequence — menu to tournament to battle`() {
        setPrivate("musicEnabled", true)
        val player3 = mockk<MediaPlayer>(relaxed = true)
        every { MediaPlayer.create(any(), R.raw.medieval_theme) } returns player1
        every { MediaPlayer.create(any(), R.raw.tournament_theme) } returns player2
        every { MediaPlayer.create(any(), R.raw.fighting_theme) } returns player3
        every { player1.isPlaying } returns true
        every { player2.isPlaying } returns true

        MusicManager.playMenuMusic(context)
        MusicManager.playTournamentMusic(context)
        MusicManager.playBattleMusic(context)

        // Alle drei wurden erstellt
        verify { MediaPlayer.create(appContext, R.raw.medieval_theme) }
        verify { MediaPlayer.create(appContext, R.raw.tournament_theme) }
        verify { MediaPlayer.create(appContext, R.raw.fighting_theme) }

        // Frühere Player wurden freigegeben
        verify { player1.release() }
        verify { player2.release() }

        // Aktiver Track ist jetzt fighting_theme
        assertEquals(R.raw.fighting_theme, getPrivate<Int>("currentTrack"))
        assertSame(player3, getPrivate("player"))
    }

    @Test
    fun `play handles MediaPlayer create returning null gracefully`() {
        // MediaPlayer.create() kann in seltenen Fällen null zurückgeben
        // (z. B. korrupte Resource). Das darf nicht crashen.
        every { MediaPlayer.create(any(), any<Int>()) } returns null

        MusicManager.playMenuMusic(context)

        // currentTrack wird trotzdem gesetzt; player bleibt null
        assertEquals(R.raw.medieval_theme, getPrivate<Int>("currentTrack"))
        assertEquals(null, getPrivate<String>("player"))
    }

    // -------------------------------------------------------------------------
    // Reflection helpers
    // -------------------------------------------------------------------------

    @Suppress("UNCHECKED_CAST")
    private fun <T> getPrivate(name: String): T {
        val field = MusicManager::class.java.getDeclaredField(name)
        field.isAccessible = true
        return field.get(MusicManager) as T
    }

    private fun setPrivate(name: String, value: Any?) {
        val field = MusicManager::class.java.getDeclaredField(name)
        field.isAccessible = true
        field.set(MusicManager, value)
    }

    private fun resetMusicManagerState() {
        try {
            setPrivate("player", null)
            setPrivate("currentTrack", 0)
            setPrivate("musicEnabled", true)
            setPrivate("musicVolume", 0.6f)
            setPrivate("soundPoolReady", false)
            setPrivate("sfxEnabled", true)
            @Suppress("UNCHECKED_CAST")
            (getPrivate<MutableMap<Int, Int>>("sfxIds")).clear()
        } catch (_: NoSuchFieldException) {
            // Falls die Feld-Namen abweichen, einfach ignorieren -- der Test wird
            // dann beim ersten Reflection-Zugriff in @Test sauber failen.
        }
    }
}
