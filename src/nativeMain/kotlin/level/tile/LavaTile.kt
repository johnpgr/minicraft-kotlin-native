package level.tile

import entity.*
import gfx.*
import level.*
import util.setSeed
import kotlin.random.Random

data class LavaTile(
    override val id: Byte,
    override val random: Random = util.uniqueRandom(),
    var wRandom: Random = util.uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = true,
    override var connectsToLava: Boolean = true,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun LavaTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    wRandom =
        wRandom.setSeed((Tile.tickCount + (x / 2 - y) * 4311) / 10 * 54687121L + x * 3271612L + y * 3412987161L)
    val col = Color.get(500, 500, 520, 550)
    val transitionColor1 =
        Color.get(3, 500, level.dirtColor - 111, level.dirtColor)
    val transitionColor2 =
        Color.get(3, 500, level.sandColor - 110, level.sandColor)

    val u = !(level.getTile(x, y - 1)?.connectsToLava ?: false)
    val d = !(level.getTile(x, y + 1)?.connectsToLava ?: false)
    val l = !(level.getTile(x - 1, y)?.connectsToLava ?: false)
    val r = !(level.getTile(x + 1, y)?.connectsToLava ?: false)

    val su = u && (level.getTile(x, y - 1)?.connectsToSand ?: false)
    val sd = d && (level.getTile(x, y + 1)?.connectsToSand ?: false)
    val sl = l && (level.getTile(x - 1, y)?.connectsToSand ?: false)
    val sr = r && (level.getTile(x + 1, y)?.connectsToSand ?: false)

    if (!u && !l) {
        screen.render(
            x * 16 + 0, y * 16 + 0, wRandom.nextInt(4), col, wRandom.nextInt(4)
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
            x * 16 + 8, y * 16 + 0, wRandom.nextInt(4), col, wRandom.nextInt(4)
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
            x * 16 + 0, y * 16 + 8, wRandom.nextInt(4), col, wRandom.nextInt(4)
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
            x * 16 + 8, y * 16 + 8, wRandom.nextInt(4), col, wRandom.nextInt(4)
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 8,
        (if (r) 16 else 15) + (if (d) 2 else 1) * 32,
        if (sd || sr) transitionColor2 else transitionColor1,
        0
    )
}

fun LavaTile.mayPass(level: Level?, x: Int, y: Int, e: Entity): Boolean {
    return e.canSwim()
}

fun LavaTile.tick(level: Level, xt: Int, yt: Int) {
    var xn = xt
    var yn = yt

    if (random.nextBoolean()) xn += random.nextInt(2) * 2 - 1
    else yn += random.nextInt(2) * 2 - 1

    if (level.getTile(xn, yn) === Tile.hole) {
        level.setTile(xn, yn, this, 0)
    }
}

fun LavaTile.getLightRadius(level: Level, x: Int, y: Int): Int {
    return 6
}
