package level.tile

import entity.*
import gfx.*
import item.*
import item.resource.Resource
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class OreTile(
    override val id: Byte,
    val toDrop: Resource,
    var color: Int = toDrop.color and 0xffff00,
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

fun OreTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    color = (toDrop.color and -0x100) + Color.get(level.dirtColor)
    screen.render(x * 16 + 0, y * 16 + 0, 17 + 1 * 32, color, 0)
    screen.render(x * 16 + 8, y * 16 + 0, 18 + 1 * 32, color, 0)
    screen.render(x * 16 + 0, y * 16 + 8, 17 + 2 * 32, color, 0)
    screen.render(x * 16 + 8, y * 16 + 8, 18 + 2 * 32, color, 0)
}

fun OreTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean {
    return false
}

fun OreTile.hurt(
    level: Level, x: Int, y: Int, source: Mob, dmg: Int, attackDir: Int
) {
    hurt(level, x, y, 0)
}

fun OreTile.interact(
    level: Level, xt: Int, yt: Int, player: Player, item: Item, attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.pickaxe) {
            if (player.payStamina(6 - item.level)) {
                hurt(level, xt, yt, 1)
                return true
            }
        }
    }
    return false
}

fun OreTile.hurt(level: Level, x: Int, y: Int, dmg: Int) {
    val damage = level.getData(x, y) + 1
    level.add(SmashParticle(x * 16 + 8, y * 16 + 8))
    level.add(
        TextParticle(
            "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)
        )
    )
    if (dmg > 0) {
        var count = random.nextInt(2)
        if (damage >= random.nextInt(10) + 3) {
            level.setTile(x, y, Tile.dirt, 0)
            count += 2
        } else {
            level.setData(x, y, damage)
        }
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(toDrop),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
    }
}

fun OreTile.bumpedInto(level: Level, x: Int, y: Int, entity: Entity) {
    entity.hurt(this, x, y, 3)
}