package level.tile

import entity.*
import gfx.*
import level.*
import item.*
import item.resource.Resource
import util.uniqueRandom
import kotlin.random.Random

data class SandTile(
    override val id: Byte,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = true,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun SandTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col = Color.get(
        level.sandColor + 2,
        level.sandColor,
        level.sandColor - 110,
        level.sandColor - 110
    )
    val transitionColor = Color.get(
        level.sandColor - 110,
        level.sandColor,
        level.sandColor - 110,
        level.dirtColor
    )

    val u = !(level.getTile(x, y - 1)?.connectsToSand ?: false)
    val d = !(level.getTile(x, y + 1)?.connectsToSand ?: false)
    val l = !(level.getTile(x - 1, y)?.connectsToSand ?: false)
    val r = !(level.getTile(x + 1, y)?.connectsToSand ?: false)

    val steppedOn = level.getData(x, y) > 0

    if (!u && !l) {
        if (!steppedOn) screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0)
        else screen.render(x * 16 + 0, y * 16 + 0, 3 + 1 * 32, col, 0)
    } else screen.render(
        x * 16 + 0,
        y * 16 + 0,
        (if (l) 11 else 12) + (if (u) 0 else 1) * 32,
        transitionColor,
        0
    )

    if (!u && !r) {
        screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0)
    } else screen.render(
        x * 16 + 8,
        y * 16 + 0,
        (if (r) 13 else 12) + (if (u) 0 else 1) * 32,
        transitionColor,
        0
    )

    if (!d && !l) {
        screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0)
    } else screen.render(
        x * 16 + 0,
        y * 16 + 8,
        (if (l) 11 else 12) + (if (d) 2 else 1) * 32,
        transitionColor,
        0
    )
    if (!d && !r) {
        if (!steppedOn) screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0)
        else screen.render(x * 16 + 8, y * 16 + 8, 3 + 1 * 32, col, 0)
    } else screen.render(
        x * 16 + 8,
        y * 16 + 8,
        (if (r) 13 else 12) + (if (d) 2 else 1) * 32,
        transitionColor,
        0
    )
}

fun SandTile.tick(level: Level, x: Int, y: Int) {
    val d = level.getData(x, y)
    if (d > 0) level.setData(x, y, d - 1)
}

fun SandTile.steppedOn(level: Level, x: Int, y: Int, entity: Entity) {
    if (entity is Mob) {
        level.setData(x, y, 10)
    }
}

fun SandTile.interact(
    level: Level, xt: Int, yt: Int, player: Player, item: Item, attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.shovel) {
            if (player.payStamina(4 - item.level)) {
                level.setTile(xt, yt, Tile.dirt, 0)
                level.add(
                    ItemEntity(
                        ResourceItem(Resource.sand),
                        xt * 16 + random.nextInt(10) + 3,
                        yt * 16 + random.nextInt(10) + 3
                    )
                )
                return true
            }
        }
    }
    return false
}