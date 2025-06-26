package level.tile

import entity.*
import gfx.*
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class SaplingTile(
    override val id: Byte,
    val onType: Tile,
    val growsTo: Tile,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = false,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
        connectsToSand = onType.connectsToSand
        connectsToGrass = onType.connectsToGrass
        connectsToWater = onType.connectsToWater
        connectsToLava = onType.connectsToLava
    }
}

fun SaplingTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    onType.render(screen, level, x, y)
    val col = Color.get(10, 40, 50, -1)
    screen.render(x * 16 + 4, y * 16 + 4, 11 + 3 * 32, col, 0)
}

fun SaplingTile.tick(level: Level, x: Int, y: Int) {
    val age = level.getData(x, y) + 1
    if (age > 100) {
        level.setTile(x, y, growsTo, 0)
    } else {
        level.setData(x, y, age)
    }
}

fun SaplingTile.hurt(
    level: Level, x: Int, y: Int, source: Mob, dmg: Int, attackDir: Int
) {
    level.setTile(x, y, onType, 0)
}
