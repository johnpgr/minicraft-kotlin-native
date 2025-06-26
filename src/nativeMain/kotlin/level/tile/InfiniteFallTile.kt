package level.tile

import entity.*
import gfx.*
import level.*
import kotlin.random.Random

data class InfiniteFallTile(
    override val id: Byte,
    override val random: Random = util.uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = false,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun InfiniteFallTile.render(screen: Screen, level: Level, x: Int, y: Int) {
}

fun InfiniteFallTile.tick(level: Level, xt: Int, yt: Int) {
}

fun InfiniteFallTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean =
    e is AirWizard
