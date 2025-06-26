package level.tile

import gfx.*
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class StairsTile(
    override val id: Byte,
    val leadsUp: Boolean,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = false,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun StairsTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val color = Color.get(level.dirtColor, 0, 333, 444)
    var xt = 0
    if (leadsUp) xt = 2
    screen.render(x * 16 + 0, y * 16 + 0, xt + 2 * 32, color, 0)
    screen.render(x * 16 + 8, y * 16 + 0, xt + 1 + 2 * 32, color, 0)
    screen.render(x * 16 + 0, y * 16 + 8, xt + 3 * 32, color, 0)
    screen.render(x * 16 + 8, y * 16 + 8, xt + 1 + 3 * 32, color, 0)
}
