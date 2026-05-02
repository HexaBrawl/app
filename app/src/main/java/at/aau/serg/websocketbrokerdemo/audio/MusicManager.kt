package at.aau.serg.websocketbrokerdemo.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.example.myapplication.R

/**
 * Globaler Manager für die Hintergrundmusik des Hauptmenüs.
 *
 * Spielt den Soundtrack im Loop ab dem Homescreen, pausiert ihn,
 * sobald wir in den Game-Screen (Kampf) wechseln, da dieser eine
 * eigene Musik bekommen wird.
 */
object MusicManager {

    private var player: MediaPlayer? = null
    private var currentTrack: Int = 0
    private var isMuted: Boolean = false

    /** Startet den Menü-Track, falls noch nicht aktiv. */
    fun playMenuMusic(context: Context) {
        play(context, R.raw.medieval_theme)
    }

    private fun play(context: Context, @RawRes resId: Int) {
        // Bereits derselbe Track aktiv -> nur sicherstellen, dass er läuft
        if (player != null && currentTrack == resId) {
            if (!isMuted && player?.isPlaying == false) {
                player?.start()
            }
            return
        }

        // Anderer Track läuft -> sauber stoppen
        release()

        currentTrack = resId
        player = MediaPlayer.create(context.applicationContext, resId)?.apply {
            isLooping = true
            setVolume(if (isMuted) 0f else 0.6f, if (isMuted) 0f else 0.6f)
            start()
        }
    }

    /** Pausiert die Musik (z. B. beim Verlassen der Lobby Richtung Spiel). */
    fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    /** Setzt eine pausierte Musik fort. */
    fun resume() {
        if (!isMuted) {
            player?.takeIf { !it.isPlaying }?.start()
        }
    }

    /** Mute / Unmute über den Settings-Button. */
    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            player?.setVolume(0f, 0f)
        } else {
            player?.setVolume(0.6f, 0.6f)
        }
    }

    fun isMuted(): Boolean = isMuted

    /** Ressourcen freigeben (z. B. in onDestroy der Activity). */
    fun release() {
        player?.apply {
            if (isPlaying) stop()
            release()
        }
        player = null
        currentTrack = 0
    }
}
