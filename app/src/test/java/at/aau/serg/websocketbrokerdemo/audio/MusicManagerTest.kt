package at.aau.serg.websocketbrokerdemo.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für MusicManager (Singleton).
 *
 * Wichtig: Da MusicManager ein object ist, teilen alle Tests dieselbe
 * Instanz. Wir resetten die internen Felder per Reflection vor jedem Test,
 * sonst beeinflussen sich die Tests gegenseitig.
 *
 * Statische Aufrufe wie MediaPlayer.create() und der SoundPool.Builder
 * werden gemockkt, damit kein echtes Audio geladen wird.
 */
class MusicManagerTest {

    private lateinit var context: Context
    private lateinit var appContext: Context
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var soundPool: SoundPool

    @BeforeEach
    fun setUp() {
        resetMusicManagerState()

        context = mockk(relaxed = true)
        appContext = mockk(relaxed = true)
        mediaPlayer = mockk(relaxed = true)
        soundPool = mockk(relaxed = true)

        every { context.applicationContext } returns appContext

        // MediaPlayer.create(Context, Int) ist statisch -> mocken
        mockkStatic(MediaPlayer::class)
        every { MediaPlayer.create(any(), any<Int>()) } returns mediaPlayer

        // SoundPool wird via Builder().build() erzeugt -> Constructor mocken
        mockkConstructor(SoundPool.Builder::class)
        every { anyConstructed<SoundPool.Builder>().setMaxStreams(any()) } returns mockk<SoundPool.Builder>(relaxed = true).also {
            every { it.setAudioAttributes(any()) } returns it
            every { it.build() } returns soundPool
        }
        every { soundPool.load(any<Context>(), any<Int>(), any()) } returns 1
        justRun { soundPool.release() }
    }

    @AfterEach
    fun tearDown() {
        resetMusicManagerState()
        unmockkAll()
    }

    // -------------------------------------------------------------------------
    // applyMusicSettings
    // -------------------------------------------------------------------------

    @Test
    fun `applyMusicSettings stores enabled and volume`() {
        MusicManager.applyMusicSettings(enabled = true, volume = 0.5f)

        assertEquals(true, getPrivate<Boolean>("musicEnabled"))
        assertEquals(0.5f, getPrivate<Float>("musicVolume"))
    }

    @Test
    fun `applyMusicSettings coerces volume above 1 down to 1`() {
        MusicManager.applyMusicSettings(enabled = true, volume = 2.5f)

        assertEquals(1.0f, getPrivate<Float>("musicVolume"))
    }

    @Test
    fun `applyMusicSettings coerces negative volume up to 0`() {
        MusicManager.applyMusicSettings(enabled = true, volume = -0.3f)

        assertEquals(0.0f, getPrivate<Float>("musicVolume"))
    }

    @Test
    fun `applyMusicSettings sets volume on existing player`() {
        // Player setzen wie nach playMenuMusic()
        setPrivate("player", mediaPlayer)
        every { mediaPlayer.isPlaying } returns false

        MusicManager.applyMusicSettings(enabled = true, volume = 0.7f)

        verify { mediaPlayer.setVolume(0.7f, 0.7f) }
    }

    @Test
    fun `applyMusicSettings disabled mutes player`() {
        setPrivate("player", mediaPlayer)
        every { mediaPlayer.isPlaying } returns true

        MusicManager.applyMusicSettings(enabled = false, volume = 0.7f)

        // Volume wird auf 0 gesetzt (mute)
        verify { mediaPlayer.setVolume(0f, 0f) }
        // Und Player wird pausiert
        verify { mediaPlayer.pause() }
    }

    @Test
    fun `applyMusicSettings re-enabled starts paused player`() {
        setPrivate("player", mediaPlayer)
        every { mediaPlayer.isPlaying } returns false

        MusicManager.applyMusicSettings(enabled = true, volume = 0.4f)

        verify { mediaPlayer.start() }
    }

    // -------------------------------------------------------------------------
    // applySfxSettings
    // -------------------------------------------------------------------------

    @Test
    fun `applySfxSettings toggles flag`() {
        MusicManager.applySfxSettings(false)
        assertFalse(getPrivate<Boolean>("sfxEnabled"))

        MusicManager.applySfxSettings(true)
        assertTrue(getPrivate<Boolean>("sfxEnabled"))
    }

    // -------------------------------------------------------------------------
    // playSfx (no-op behaviour)
    // -------------------------------------------------------------------------

    @Test
    fun `playSfx does nothing when sfx disabled`() {
        setPrivate("soundPoolReady", true)
        setPrivate("soundPool", soundPool)
        MusicManager.applySfxSettings(false)

        MusicManager.playSfx(42)

        verify(exactly = 0) { soundPool.play(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `playSfx does nothing when soundPool not ready`() {
        setPrivate("soundPoolReady", false)
        MusicManager.applySfxSettings(true)

        MusicManager.playSfx(42)

        verify(exactly = 0) { soundPool.play(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `playSfx does nothing for unknown res id`() {
        setPrivate("soundPoolReady", true)
        setPrivate("soundPool", soundPool)
        MusicManager.applySfxSettings(true)
        // sfxIds enthält keine Einträge

        MusicManager.playSfx(9999)

        verify(exactly = 0) { soundPool.play(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `playSfx invokes soundPool when enabled and id known`() {
        setPrivate("soundPoolReady", true)
        setPrivate("soundPool", soundPool)
        @Suppress("UNCHECKED_CAST")
        val ids = getPrivate<MutableMap<Int, Int>>("sfxIds")
        ids[123] = 5
        MusicManager.applySfxSettings(true)

        MusicManager.playSfx(123)

        verify { soundPool.play(5, 1f, 1f, 1, 0, 1f) }
    }

    // -------------------------------------------------------------------------
    // pause / resume
    // -------------------------------------------------------------------------

    @Test
    fun `pause does nothing when no player`() {
        // player ist null -> pause sollte nicht crashen
        MusicManager.pause()
        // (kein verify nötig - es darf nur nichts werfen)
    }

    @Test
    fun `pause pauses an active player`() {
        setPrivate("player", mediaPlayer)
        every { mediaPlayer.isPlaying } returns true

        MusicManager.pause()

        verify { mediaPlayer.pause() }
    }

    @Test
    fun `pause is a no-op for already-paused player`() {
        setPrivate("player", mediaPlayer)
        every { mediaPlayer.isPlaying } returns false

        MusicManager.pause()

        verify(exactly = 0) { mediaPlayer.pause() }
    }

    @Test
    fun `resume starts paused player when music enabled`() {
        setPrivate("player", mediaPlayer)
        setPrivate("musicEnabled", true)
        every { mediaPlayer.isPlaying } returns false

        MusicManager.resume()

        verify { mediaPlayer.start() }
    }

    @Test
    fun `resume does nothing when music disabled`() {
        setPrivate("player", mediaPlayer)
        setPrivate("musicEnabled", false)
        every { mediaPlayer.isPlaying } returns false

        MusicManager.resume()

        verify(exactly = 0) { mediaPlayer.start() }
    }

    // -------------------------------------------------------------------------
    // release
    // -------------------------------------------------------------------------

    @Test
    fun `release stops and frees an active player`() {
        setPrivate("player", mediaPlayer)
        every { mediaPlayer.isPlaying } returns true

        MusicManager.release()

        verify { mediaPlayer.stop() }
        verify { mediaPlayer.release() }
        assertNull(getPrivate<MediaPlayer?>("player"))
    }

    @Test
    fun `release frees soundPool when ready`() {
        setPrivate("soundPoolReady", true)
        setPrivate("soundPool", soundPool)

        MusicManager.release()

        verify { soundPool.release() }
        assertFalse(getPrivate<Boolean>("soundPoolReady"))
    }

    @Test
    fun `release is safe when nothing was initialised`() {
        setPrivate("soundPoolReady", false)
        setPrivate("player", null)

        // Darf nicht crashen
        MusicManager.release()
    }

    // -------------------------------------------------------------------------
    // Reflection helpers (because MusicManager is an object with private state)
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
        setPrivate("player", null)
        setPrivate("currentTrack", 0)
        setPrivate("musicEnabled", true)
        setPrivate("musicVolume", 0.6f)
        setPrivate("soundPoolReady", false)
        setPrivate("sfxEnabled", true)
        @Suppress("UNCHECKED_CAST")
        (getPrivate<MutableMap<Int, Int>>("sfxIds")).clear()
    }
}
