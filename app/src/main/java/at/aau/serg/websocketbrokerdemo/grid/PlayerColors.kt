package at.aau.serg.websocketbrokerdemo.grid
import android.graphics.Color

object PlayerColors {
    private val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA)
    private val playerColors = mutableMapOf<String, Int>()

    fun getColorForPlayer(player: String): Int {
        return playerColors.getOrPut(player) {
            colors[playerColors.size % colors.size]
        }
    }
}