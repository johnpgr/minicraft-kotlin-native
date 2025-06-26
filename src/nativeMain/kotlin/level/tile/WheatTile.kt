package level.tile

import entity.*
import gfx.*
import item.*
import item.resource.Resource
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class WheatTile(
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

fun WheatTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val age = level.getData(x, y)
    var col = Color.get(
        level.dirtColor - 121, level.dirtColor - 11, level.dirtColor, 50
    )
    var icon = age / 10
    if (icon >= 3) {
        col = Color.get(
            level.dirtColor - 121,
            level.dirtColor - 11,
            50 + (icon) * 100,
            40 + (icon - 3) * 2 * 100
        )
        if (age == 50) {
            col = Color.get(0, 0, 50 + (icon) * 100, 40 + (icon - 3) * 2 * 100)
        }
        icon = 3
    }

    screen.render(x * 16 + 0, y * 16 + 0, 4 + 3 * 32 + icon, col, 0)
    screen.render(x * 16 + 8, y * 16 + 0, 4 + 3 * 32 + icon, col, 0)
    screen.render(x * 16 + 0, y * 16 + 8, 4 + 3 * 32 + icon, col, 1)
    screen.render(x * 16 + 8, y * 16 + 8, 4 + 3 * 32 + icon, col, 1)
}

fun WheatTile.tick(level: Level, xt: Int, yt: Int) {
    if (random.nextInt(2) == 0) return

    val age = level.getData(xt, yt)
    if (age < 50) level.setData(xt, yt, age + 1)
}

fun WheatTile.interact(
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

fun WheatTile.steppedOn(level: Level, xt: Int, yt: Int, entity: Entity) {
    if (random.nextInt(60) != 0) return
    if (level.getData(xt, yt) < 2) return
    harvest(level, xt, yt)
}

fun WheatTile.hurt(
    level: Level, x: Int, y: Int, source: Mob, dmg: Int, attackDir: Int
) {
    harvest(level, x, y)
}

private fun WheatTile.harvest(level: Level, x: Int, y: Int) {
    val age = level.getData(x, y)

    var count: Int = random.nextInt(2)
    for (i in 0..<count) {
        level.add(
            ItemEntity(
                ResourceItem(Resource.seeds),
                x * 16 + random.nextInt(10) + 3,
                y * 16 + random.nextInt(10) + 3
            )
        )
    }

    count = 0
    if (age == 50) {
        count = random.nextInt(3) + 2
    } else if (age >= 40) {
        count = random.nextInt(2) + 1
    }
    for (i in 0..<count) {
        level.add(
            ItemEntity(
                ResourceItem(Resource.wheat),
                x * 16 + random.nextInt(10) + 3,
                y * 16 + random.nextInt(10) + 3
            )
        )
    }

    level.setTile(x, y, Tile.dirt, 0)
}