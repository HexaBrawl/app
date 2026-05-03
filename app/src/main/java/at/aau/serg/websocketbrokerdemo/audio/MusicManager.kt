package at.aau.serg.websocketbrokerdemo.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import com.example.myapplication.R



/**
 * Globaler Manager für die gesamte Audio-Ausgabe der App.
 *
 *  - Hintergrundmusik im Menü (Loop, MediaPlayer)
 *  - Soundeffekte im Spiel (SoundPool – mehrere parallel, niedrige Latenz)
 *
 * Konfigurierbar über die Settings:
 *   musicEnabled, musicVolume, sfxEnabled
 *
 * Wichtig: applyMusicSettings() / applySfxSettings() müssen aufgerufen werden,
 * sobald die Settings sich ändern (geschieht automatisch via SettingsViewModel).
 */
object MusicManager {

    // --- Music ---
    private var player: MediaPlayer? = null
    private var currentTrack: Int = 0

    private var musicEnabled: Boolean = true
    private var musicVolume: Float = 0.6f

    // --- SFX ---
    private lateinit var soundPool: SoundPool
    private var soundPoolReady = false
    private val sfxIds: MutableMap<Int, Int> = mutableMapOf()  // resId -> soundId
    private var sfxEnabled: Boolean = true

    // -------------------------------------------------------------------------
    // Lifecycle / Init
    // -------------------------------------------------------------------------

    /** Einmalig beim App-Start aufrufen (z. B. in MainActivity.onCreate). */
    fun init(context: Context) {
        if (soundPoolReady) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)            // bis zu 6 SFX gleichzeitig
            .setAudioAttributes(attrs)
            .build()
        soundPoolReady = true

        // SFX vorab laden -> erster Trigger ist nicht laggy
        preloadSfx(context, R.raw.sfx_sword_block)
    }

    private fun preloadSfx(context: Context, @RawRes resId: Int) {
        if (!sfxIds.containsKey(resId)) {
            sfxIds[resId] = soundPool.load(context.applicationContext, resId, 1)
        }
    }

    // -------------------------------------------------------------------------
    // Music
    // -------------------------------------------------------------------------

    /** Startet den Menü-Track, sofern aktiviert. */
    fun playMenuMusic(context: Context) {
        play(context, R.raw.medieval_theme)
    }

    private fun play(context: Context, @RawRes resId: Int) {
        if (player != null && currentTrack == resId) {
            applyVolumeToPlayer()
            if (musicEnabled && player?.isPlaying == false) player?.start()
            return
        }
        releaseMusic()
        currentTrack = resId
        player = MediaPlayer.create(context.applicationContext, resId)?.apply {
            isLooping = true
            applyVolumeToPlayer(this)
            if (musicEnabled) start()
        }
    }

    fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    fun resume() {
        if (musicEnabled) {
            player?.takeIf { !it.isPlaying }?.start()
        }
    }

    /** Wird vom SettingsViewModel aufgerufen, wenn sich Musik-Settings ändern. */
    fun applyMusicSettings(enabled: Boolean, volume: Float) {
        musicEnabled = enabled
        musicVolume = volume.coerceIn(0f, 1f)
        applyVolumeToPlayer()
        if (!enabled) {
            player?.takeIf { it.isPlaying }?.pause()
        } else {
            player?.takeIf { !it.isPlaying }?.start()
        }
    }

    private fun applyVolumeToPlayer(target: MediaPlayer? = player) {
        val v = if (musicEnabled) musicVolume else 0f
        target?.setVolume(v, v)
    }

    private fun releaseMusic() {
        player?.apply {
            if (isPlaying) stop()
            release()
        }
        player = null
        currentTrack = 0
    }

    // -------------------------------------------------------------------------
    // SFX
    // -------------------------------------------------------------------------

    /** Spielt einen SFX-Effekt ab (no-op wenn deaktiviert). */
    fun playSfx(@RawRes resId: Int) {
        if (!sfxEnabled || !soundPoolReady) return
        val id = sfxIds[resId] ?: return
        soundPool.play(id, 1f, 1f, 1, 0, 1f)
    }

    /** Convenience – Schwert-Block-Sound für Truppenkampf. */
    fun playSwordBlock() = playSfx(R.raw.sfx_sword_block)

    fun applySfxSettings(enabled: Boolean) {
        sfxEnabled = enabled
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    fun release() {
        releaseMusic()
        if (soundPoolReady) {
            soundPool.release()
            soundPoolReady = false
            sfxIds.clear()
        }
    }
}