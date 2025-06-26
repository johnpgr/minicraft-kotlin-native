package level.tile

import gfx.*
import entity.*
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class HoleTile(
    override val id: Byte,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = true,
    override var connectsToLava: Boolean = true,
    override var connectsToWater: Boolean = true,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun HoleTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col: Int = Color.get(111, 111, 110, 110)
    val transitionColor1: Int =
        Color.get(3, 111, level.dirtColor - 111, level.dirtColor)
    val transitionColor2: Int =
        Color.get(3, 111, level.sandColor - 110, level.sandColor)

    val u = !(level.getTile(x, y - 1)?.connectsToLiquid() ?: false)
    val d = !(level.getTile(x, y + 1)?.connectsToLiquid() ?: false)
    val l = !(level.getTile(x - 1, y)?.connectsToLiquid() ?: false)
    val r = !(level.getTile(x + 1, y)?.connectsToLiquid() ?: false)

    val su = u && (level.getTile(x, y - 1)?.connectsToSand ?: false)
    val sd = d && (level.getTile(x, y + 1)?.connectsToSand ?: false)
    val sl = l && (level.getTile(x - 1, y)?.connectsToSand ?: false)
    val sr = r && (level.getTile(x + 1, y)?.connectsToSand ?: false)

    if (!u && !l) {
        screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0)
    } else screen.render(
        x * 16 + 0,
        y * 16 + 0,
        (if (l) 14 else 15) + (if (u) 0 else 1) * 32,
        if (su || sl) transitionColor2 else transitionColor1,
        0
    )

    if (!u && !r) {
        screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0)
    } else screen.render(
        x * 16 + 8,
        y * 16 + 0,
        (if (r) 16 else 15) + (if (u) 0 else 1) * 32,
        if (su || sr) transitionColor2 else transitionColor1,
        0
    )

    if (!d && !l) {
        screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0)
    } else screen.render(
        x * 16 + 0,
        y * 16 + 8,
        (if (l) 14 else 15) + (if (d) 2 else 1) * 32,
        if (sd || sl) transitionColor2 else transitionColor1,
        0
    )
    if (!d && !r) {
        screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0)
    } else screen.render(
        x * 16 + 8,
        y * 16 + 8,
        (if (r) 16 else 15) + (if (d) 2 else 1) * 32,
        if (sd || sr) transitionColor2 else transitionColor1,
        0
    )
}

fun HoleTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean {
    return e.canSwim()
}