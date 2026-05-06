package at.aau.serg.websocketbrokerdemo.ui.game

import MyStomp
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.grid.GridShape
import at.aau.serg.websocketbrokerdemo.grid.HexGrid
import at.aau.serg.websocketbrokerdemo.grid.UnitData
import com.example.myapplication.R
import kotlin.math.max

/**
 * GameScreen mit Zoom + Pan auf eine vergrößerte Karte.
 *
 * Wie die Camera funktioniert:
 *  - Hintergrund + Grid liegen in einer Box, die größer ist als der Screen
 *    (siehe MAP_SIZE_FACTOR). Dadurch hat der Spieler "Reserve" beim Pannen.
 *  - scale (Zoom)  : 1.0f = Karte füllt Screen, 2.5f = stark herangezoomt
 *  - offsetX/Y     : Pan in Screen-Pixeln
 *  - Beim Pinch-Zoom passen wir den Pan so an, dass der Pivot-Punkt
 *    (Mitte der zwei Finger) seine Bildschirmposition behält -- so fühlt
 *    sich der Zoom natürlich an, ohne ungewollten "Drift".
 *  - Pan wird begrenzt damit man nie über den Kartenrand hinaus pannen kann.
 */
@Composable
fun GameScreen(myStomp: MyStomp, responseState: State<String>) {

    // Wie viel größer die Karte als der Screen ist (1.0 = Screen-Size).
    // 1.6 = 60% extra Fläche zum Erkunden bei Zoom = 1.0
    val mapSizeFactor = 1.0f

    // Zoom-Limits
    val minScale = 1.0f   // 1.0 = ganze (vergrößerte) Karte ist sichtbar
    val maxScale = 4.0f

    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Tatsächliche View-Größe in Pixeln (für Pan-Begrenzung)
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    /**
     * Begrenzt den Pan, damit beim aktuellen Zoom der Kartenrand nie
     * "über die Bildschirmkante hinaus" gezogen werden kann.
     *
     * Mathematisch: bei scale=s ist die Karte (mapSizeFactor * viewportSize * s)
     * groß. Sie darf maximal so weit verschoben werden, dass ihr Rand bündig
     * mit dem Screen-Rand abschließt.
     */
    fun clampOffset(x: Float, y: Float): Pair<Float, Float> {
        if (viewportSize == IntSize.Zero) return x to y
        // Wie viel die Karte größer als der Viewport ist (in jeder Richtung)
        val excessX = (viewportSize.width * mapSizeFactor * scale - viewportSize.width) / 2f
        val excessY = (viewportSize.height * mapSizeFactor * scale - viewportSize.height) / 2f
        // Wenn die Karte kleiner ist als der Viewport (kann bei mapSizeFactor<1 passieren),
        // dann nicht weit auseinanderdriften lassen.
        val maxX = max(0f, excessX)
        val maxY = max(0f, excessY)
        return x.coerceIn(-maxX, maxX) to y.coerceIn(-maxY, maxY)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🎮 CAMERA LAYER
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { viewportSize = it }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        // Neuer Zoom (vor Anwendung), für Pivot-Berechnung
                        val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                        val effectiveZoom = newScale / scale  // realer Zoom-Faktor nach Clamp

                        // Pivot-Korrektur: Beim Zoom soll der Punkt unter den Fingern
                        // an Ort und Stelle bleiben. Dazu verschieben wir den Offset
                        // um die Differenz, die durch den Zoom entsteht.
                        //
                        // centroid ist relativ zum Composable, wir wollen ihn aber
                        // relativ zum Mittelpunkt umrechnen (weil graphicsLayer
                        // standardmäßig vom Center aus skaliert).
                        val pivotX = centroid.x - viewportSize.width / 2f
                        val pivotY = centroid.y - viewportSize.height / 2f

                        // Neue Offsets: Pan + Pivot-Korrektur
                        val newOffsetX = (offsetX + pan.x) -
                                (pivotX - offsetX) * (effectiveZoom - 1f)
                        val newOffsetY = (offsetY + pan.y) -
                                (pivotY - offsetY) * (effectiveZoom - 1f)

                        // State updaten
                        scale = newScale
                        val (clampedX, clampedY) = run {
                            // Erst Scale anwenden, dann clampen mit dem neuen Scale
                            val tempScale = scale
                            val excessX = (viewportSize.width * mapSizeFactor * tempScale - viewportSize.width) / 2f
                            val excessY = (viewportSize.height * mapSizeFactor * tempScale - viewportSize.height) / 2f
                            val maxX = max(0f, excessX)
                            val maxY = max(0f, excessY)
                            newOffsetX.coerceIn(-maxX, maxX) to newOffsetY.coerceIn(-maxY, maxY)
                        }
                        offsetX = clampedX
                        offsetY = clampedY
                    }
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {

            // 🖼️ BACKGROUND -- größer als der Viewport (mapSizeFactor)
            // Er füllt die Box, die durch fillMaxSize() den Viewport hat,
            // aber wir vergrößern den Inhalt mit fillMaxSize(mapSizeFactor)
            // und zentrieren ihn über die Box.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_map2),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(mapSizeFactor),
                    contentScale = ContentScale.Crop
                )
            }

            // 🧩 GRID (liegt drüber, gleiche Transformation)
            HexGrid(
                modifier = Modifier.fillMaxSize(mapSizeFactor),
                units = listOf(
                    UnitData(3, 4, "Player 1"),
                    UnitData(5, 2, "Player 2")
                ),
                shape = GridShape.RECTANGLE,
                onHexClicked = { x, y ->
                    println("Hex clicked: $x, $y")
                }
            )
        }

        // 📱 UI (fix, nicht zoombar)
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Server Response: ${responseState.value}")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { myStomp.connect() }) { Text("Connect") }
                    Button(onClick = { myStomp.sendHello() }) { Text("Send Hello") }
                    Button(onClick = { myStomp.sendJson() }) { Text("Send JSON") }
                }
            }
        }
    }
}