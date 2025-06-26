package level.tile

import entity.*
import gfx.*
import item.Item
import item.ToolItem
import item.ToolType
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class FarmTile(
    override val id: Byte,
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

fun FarmTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col = Color.get(
        level.dirtColor - 121,
        level.dirtColor - 11,
        level.dirtColor,
        level.dirtColor + 111
    )
    screen.render(x * 16 + 0, y * 16 + 0, 2 + 32, col, 1)
    screen.render(x * 16 + 8, y * 16 + 0, 2 + 32, col, 0)
    screen.render(x * 16 + 0, y * 16 + 8, 2 + 32, col, 0)
    screen.render(x * 16 + 8, y * 16 + 8, 2 + 32, col, 1)
}

fun FarmTile.interact(
    level: Level, xt: Int, yt: Int, player: Player, item: Item, attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.shovel) {
            if (player.payStamina(4 - item.level)) {
                level.setTile(xt, yt, Tile.dirt, 0)
                return true
            }
        }
    }
    return false
}

fun FarmTile.tick(level: Level, xt: Int, yt: Int) {
    val age = level.getData(xt, yt)
    if (age < 5) level.setData(xt, yt, age + 1)
}

fun FarmTile.steppedOn(level: Level, xt: Int, yt: Int, entity: Entity) {
    if (random.nextInt(60) != 0) return
    if (level.getData(xt, yt) < 5) return
    level.setTile(xt, yt, Tile.dirt, 0)
}

