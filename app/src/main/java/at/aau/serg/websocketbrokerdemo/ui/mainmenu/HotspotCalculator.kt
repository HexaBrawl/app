package at.aau.serg.websocketbrokerdemo.ui.mainmenu

/**
 * Rechnet die Pixel-Position von Hauptmenue-Hotspots auf dem
 * Hintergrundbild aus.
 *
 * Hotspots sind als relative Anteile (0..1) der Bildflaeche definiert. Da das
 * Bild seitenverhaeltnis-erhaltend zentriert eingepasst wird (Fit),
 * beruecksichtigt [computeCenter] das Verhaeltnis von Bildmass
 * ([IMAGE_WIDTH] x [IMAGE_HEIGHT]) zum Viewport und die dadurch entstehenden
 * Raender (Letterboxing), damit Marker exakt auf den gemeinten Bildpunkten
 * sitzen.
 */
object HotspotCalculator {

    private const val IMAGE_WIDTH = 1024f
    private const val IMAGE_HEIGHT = 1536f

    fun computeCenter(
        parentW: Float,
        parentH: Float,
        xPct: Float,
        yPct: Float
    ): Pair<Float, Float> {

        val imgAspect = IMAGE_WIDTH / IMAGE_HEIGHT
        val screenAspect = parentW / parentH

        val imgW: Float
        val imgH: Float
        val padX: Float
        val padY: Float

        if (screenAspect > imgAspect) {
            // Screen breiter → Bild höhenbegrenzt
            imgH = parentH
            imgW = parentH * imgAspect
            padX = (parentW - imgW) / 2f
            padY = 0f
        } else {
            // Screen schmaler → Bild breitenbegrenzt
            imgW = parentW
            imgH = parentW / imgAspect
            padX = 0f
            padY = (parentH - imgH) / 2f
        }

        val centerX = padX + imgW * xPct
        val centerY = padY + imgH * yPct

        return centerX to centerY
    }
}
