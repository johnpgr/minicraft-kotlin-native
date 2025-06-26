package level.tile

import entity.*
import gfx.*
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class WaterTile(
    override val id: Byte,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = true,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = true,
    var wRandom: Random = uniqueRandom(),
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun WaterTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    wRandom =
        Random((Tile.tickCount + (x / 2 - y) * 4311) / 10 * 54687121L + x * 3271612L + y * 3412987161L)
    val col: Int = Color.get(5, 5, 115, 115)
    val transitionColor1: Int =
        Color.get(3, 5, level.dirtColor - 111, level.dirtColor)
    val transitionColor2: Int =
        Color.get(3, 5, level.sandColor - 110, level.sandColor)

    val u: Boolean = !(level.getTile(x, y - 1)?.connectsToWater ?: false)
    val d: Boolean = !(level.getTile(x, y + 1)?.connectsToWater ?: false)
    val l: Boolean = !(level.getTile(x - 1, y)?.connectsToWater ?: false)
    val r: Boolean = !(level.getTile(x + 1, y)?.connectsToWater ?: false)

    val su = u && (level.getTile(x, y - 1)?.connectsToSand ?: false)
    val sd = d && (level.getTile(x, y + 1)?.connectsToSand ?: false)
    val sl = l && (level.getTile(x - 1, y)?.connectsToSand ?: false)
    val sr = r && (level.getTile(x + 1, y)?.connectsToSand ?: false)

    if (!u && !l) {
        screen.render(
            x * 16 + 0,
            y * 16 + 0,
            wRandom.nextInt(4),
            col,
            wRandom.nextInt(4)
        )
    } else screen.render(
        x * 16 + 0,
        y * 16 + 0,
        (if (l) 14 else 15) + (if (u) 0 else 1) * 32,
        if (su || sl) transitionColor2 else transitionColor1,
        0
    )

    if (!u && !r) {
        screen.render(
            x * 16 + 8,
            y * 16 + 0,
            wRandom.nextInt(4),
            col,
            wRandom.nextInt(4)
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 0,
        (if (r) 16 else 15) + (if (u) 0 else 1) * 32,
        if (su || sr) transitionColor2 else transitionColor1,
        0
    )

    if (!d && !l) {
        screen.render(
            x * 16 + 0,
            y * 16 + 8,
            wRandom.nextInt(4),
            col,
            wRandom.nextInt(4)
        )
    } else screen.render(
        x * 16 + 0,
        y * 16 + 8,
        (if (l) 14 else 15) + (if (d) 2 else 1) * 32,
        if (sd || sl) transitionColor2 else transitionColor1,
        0
    )
    if (!d && !r) {
        screen.render(
            x * 16 + 8,
            y * 16 + 8,
            wRandom.nextInt(4),
            col,
            wRandom.nextInt(4)
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 8,
        (if (r) 16 else 15) + (if (d) 2 else 1) * 32,
        if (sd || sr) transitionColor2 else transitionColor1,
        0
    )
}

fun WaterTile.mayPass(level: Level?, x: Int, y: Int, e: Entity): Boolean {
    return e.canSwim()
}

fun WaterTile.tick(level: Level, xt: Int, yt: Int) {
    var xn = xt
    var yn = yt

    if (random.nextBoolean()) xn += random.nextInt(2) * 2 - 1
    else yn += random.nextInt(2) * 2 - 1

    if (level.getTile(xn, yn) == Tile.hole) {
        level.setTile(xn, yn, this, 0)
    }
}