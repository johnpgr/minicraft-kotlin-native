package level.tile

import kotlin.random.Random
import gfx.*
import entity.*
import item.ResourceItem
import item.resource.Resource
import level.*

data class CactusTile(
    override val id: Byte,
    override val random: Random = util.uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = true,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun CactusTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col: Int = Color.get(20, 40, 50, level.sandColor)
    screen.render(x * 16 + 0, y * 16 + 0, 8 + 2 * 32, col, 0)
    screen.render(x * 16 + 8, y * 16 + 0, 9 + 2 * 32, col, 0)
    screen.render(x * 16 + 0, y * 16 + 8, 8 + 3 * 32, col, 0)
    screen.render(x * 16 + 8, y * 16 + 8, 9 + 3 * 32, col, 0)
}

fun CactusTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean = false

fun CactusTile.hurt(
    level: Level, x: Int, y: Int, source: Mob, dmg: Int, attackDir: Int
) {
    val damage: Int = level.getData(x, y) + dmg
    level.add(SmashParticle(x * 16 + 8, y * 16 + 8))
    level.add(
        TextParticle(
            "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)
        )
    )
    if (damage >= 10) {
        val count: Int = random.nextInt(2) + 1
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(Resource.cactusFlower),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
        level.setTile(x, y, Tile.sand, 0)
    } else {
        level.setData(x, y, damage)
    }
}

fun CactusTile.bumpedInto(level: Level, x: Int, y: Int, entity: Entity) {
    entity.hurt(this, x, y, 1)
}

fun CactusTile.tick(level: Level, xt: Int, yt: Int) {
    val damage: Int = level.getData(xt, yt)
    if (damage > 0) level.setData(xt, yt, damage - 1)
}