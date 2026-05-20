package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.grid.input.HexInput
import at.aau.serg.websocketbrokerdemo.grid.layout.HexLayout
import at.aau.serg.websocketbrokerdemo.grid.library.GridLibrary
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import at.aau.serg.websocketbrokerdemo.grid.model.UnitData
import at.aau.serg.websocketbrokerdemo.grid.renderer.ComposeHexDrawer
import at.aau.serg.websocketbrokerdemo.grid.renderer.HexRenderer
import at.aau.serg.websocketbrokerdemo.grid.shape.RectangleShape
import at.aau.serg.websocketbrokerdemo.grid.ui.UniversalGrid
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.camera.cameraControls
import com.example.myapplication.R
import kotlinx.coroutines.delay

/**
 * Renders the live `GameState` from the server and forwards taps as `/app/move`
 * messages. Click an own unit to select it, then click a target hex to move.
 *
 * Tap handling note: the hex Canvas measures itself with `wrapContentSize`
 * (effectively 0x0) and the camera applies a `graphicsLayer` scale/translate
 * to the whole map. The inner Canvas would never see taps. We therefore catch
 * taps on the outer Box and undo the camera transform before asking the
 * `HexLayout` which cell was hit.
 */
@Composable
fun GameScreen(
    session: GameSession,
    playerCount: Int = 2
) {
    val mapSizeFactor = 1.0f
    val camera = remember { CameraState(mapSizeFactor = mapSizeFactor) }
    val spec = GridLibrary.forPlayers(playerCount)

    val gameState by session.gameState
    val lastError by session.lastError
    val localName = session.localPlayerName.value
    val botName = session.botPlayerName.value

    var selected by remember { mutableStateOf<GameUnit?>(null) }
    var lastTap by remember { mutableStateOf<String>("-") }
    var lastMove by remember { mutableStateOf<String>("-") }

    LaunchedEffect(Unit) {
        if (gameState == null) session.endpoint.requestInitialState()
    }

    LaunchedEffect(gameState?.currentTurn, gameState?.status, botName) {
        val state = gameState ?: return@LaunchedEffect
        val bot = botName ?: return@LaunchedEffect
        if (state.status != GameStatus.IN_PROGRESS) return@LaunchedEffect
        if (state.currentTurn != bot) return@LaunchedEffect

        delay(800)
        val move = pickBotMove(state, bot)
        if (move != null) session.endpoint.sendMove(move)
    }

    val units: List<GameUnit> = gameState?.units.orEmpty()
    val unitData = remember(units) {
        units
            .filter { it.type != UnitType.SKELETON }
            .map { UnitData(it.x, it.y, it.player) }
    }

    val layout = remember(spec) {
        HexLayout(hexSize = 60f, rows = spec.rows, cols = spec.cols)
    }

    // Translate a tap on the outer Box into a grid cell.
    //
    // Important: this `pointerInput` sits *after* the `graphicsLayer` from
    // `cameraControls` in the modifier chain, which means Compose has already
    // applied the inverse layer transform to the offset for us. We must NOT
    // invert again - the tap is in the pre-transform (layout) coordinate
    // space. We only need to shift the origin to the center, because the
    // inner Box has `contentAlignment = Center` and the Canvas's drawing
    // origin sits at (W/2, H/2).
    fun tapToCell(tapX: Float, tapY: Float): Pair<Int, Int>? {
        val vp = camera.viewportSize.value
        if (vp == IntSize.Zero) return null
        val gridX = tapX - vp.width / 2f
        val gridY = tapY - vp.height / 2f
        return layout.pixelToCell(gridX, gridY)
    }

    fun onCellTapped(col: Int, row: Int) {
        val current = selected
        val clickedUnit = units.firstOrNull {
            it.x == col && it.y == row && it.type != UnitType.SKELETON
        }
        lastTap = "($col,$row) " + when {
            clickedUnit == null -> "empty"
            clickedUnit.player == localName -> "own ${clickedUnit.type}"
            else -> "enemy ${clickedUnit.type}"
        }

        when {
            current == null && clickedUnit != null && clickedUnit.player == localName -> {
                selected = clickedUnit
            }
            current != null && clickedUnit != null && clickedUnit.player == localName -> {
                selected = clickedUnit
            }
            current != null -> {
                val move = Move(
                    player = current.player,
                    type = current.type,
                    fromX = current.x,
                    fromY = current.y,
                    toX = col,
                    toY = row
                )
                lastMove = "${current.type} (${current.x},${current.y}) -> ($col,$row)"
                session.lastError.value = null
                session.endpoint.sendMove(move)
                selected = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { camera.viewportSize.value = it }
            .cameraControls(camera)
            .pointerInput(spec, units, localName, selected) {
                detectTapGestures { offset ->
                    val cell = tapToCell(offset.x, offset.y) ?: return@detectTapGestures
                    onCellTapped(cell.first, cell.second)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_map2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (camera.viewportSize.value != IntSize.Zero) {

            val gridModel = GridModel(
                width = spec.cols,
                height = spec.rows,
                shape = RectangleShape,
                layout = layout,
                units = unitData
            )

            val drawer = remember { ComposeHexDrawer() }
            val renderer = remember { HexRenderer(drawer) }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                UniversalGrid(
                    model = gridModel,
                    renderer = renderer,
                    input = HexInput,
                    // Inner Canvas has 0x0 size, so it never sees taps; outer Box handles them.
                    onCellClicked = { _, _ -> },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        LaunchedEffect(spec, camera.viewportSize.value) {
            if (camera.viewportSize.value != IntSize.Zero) {

                camera.offsetX.floatValue = 0f
                camera.offsetY.floatValue = 0f

                val gridWidth = (spec.cols - 1) * (60f * 1.5f)
                val gridHeight = (spec.rows - 1) * (60f * 1.732f)

                val scaleX = camera.viewportSize.value.width / gridWidth
                val scaleY = camera.viewportSize.value.height / gridHeight

                camera.scale.floatValue = minOf(scaleX, scaleY) * 0.7f
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .background(Color(0xCC000000))
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val turn = gameState?.currentTurn ?: "-"
            val status = gameState?.status?.name ?: "DISCONNECTED"
            val mineTag = if (gameState?.currentTurn == localName) "  (YOUR TURN)" else ""
            val rxStates = session.gameStateReceivedCount.intValue
            val rxErrors = session.errorReceivedCount.intValue
            Text(
                text = "You: $localName    Bot: ${botName ?: "-"}",
                color = Color(0xFFB0B0B0)
            )
            Text(
                text = "Status: $status | Turn: $turn$mineTag",
                color = if (gameState?.currentTurn == localName) Color.Green else Color.White
            )
            Text(
                text = "RX game-states: $rxStates    RX errors: $rxErrors",
                color = Color(0xFF80C0FF)
            )
            Text(
                text = "Last tap: $lastTap",
                color = Color(0xFFB0B0B0)
            )
            Text(
                text = "Last move sent: $lastMove",
                color = Color(0xFFB0B0B0)
            )
            Text(
                text = "Units: " + units.joinToString { "${it.player.take(4)}:${it.type.name.take(3)}(${it.x},${it.y})" },
                color = Color(0xFF8090A0),
                maxLines = 3
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { session.endpoint.requestInitialState() }) {
                    Text("Refresh /app/init")
                }
                Button(onClick = { session.lastError.value = null }) {
                    Text("Clear error")
                }
            }
            lastError?.let {
                Text(
                    text = "ERROR: ${it.errorCode}: ${it.message}",
                    color = Color.Red
                )
            }
            selected?.let {
                Text(
                    text = "Selected: ${it.type} @ (${it.x}, ${it.y}) - tap a hex to move",
                    color = Color.Yellow
                )
            }
        }
    }
}

/**
 * Very small "AI": pick the first own unit that has at least one neighbouring
 * tile that is either empty or occupied by an enemy. Avoids tiles occupied by
 * own units (the backend would reject those with INVALID_MOVE).
 */
private fun pickBotMove(state: GameState, botName: String): Move? {
    val occupied = state.units
        .filter { it.type != UnitType.SKELETON }
        .associateBy { it.x to it.y }

    val botUnits = state.units
        .filter { it.player == botName && it.type != UnitType.SKELETON }
        .shuffled()

    val directions = listOf(
        1 to 0, -1 to 0,
        0 to 1, 0 to -1,
        1 to 1, -1 to -1
    ).shuffled()

    botUnits.forEach { unit ->
        directions.forEach { (dx, dy) ->
            val tx = unit.x + dx
            val ty = unit.y + dy
            if (tx < 0 || ty < 0) return@forEach
            val target = occupied[tx to ty]
            if (target == null || target.player != botName) {
                return Move(
                    player = botName,
                    type = unit.type,
                    fromX = unit.x,
                    fromY = unit.y,
                    toX = tx,
                    toY = ty
                )
            }
        }
    }
    return null
}
