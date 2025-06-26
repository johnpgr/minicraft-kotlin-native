package level.tile

import entity.*
import gfx.*
import item.Item
import item.ToolItem
import item.ToolType
import level.*
import kotlin.random.Random

data class CloudCactusTile(
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

fun CloudCactusTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val color = Color.get(444, 111, 333, 555)
    screen.render(x * 16 + 0, y * 16 + 0, 17 + 1 * 32, color, 0)
    screen.render(x * 16 + 8, y * 16 + 0, 18 + 1 * 32, color, 0)
    screen.render(x * 16 + 0, y * 16 + 8, 17 + 2 * 32, color, 0)
    screen.render(x * 16 + 8, y * 16 + 8, 18 + 2 * 32, color, 0)
}

fun CloudCactusTile.mayPass(
    level: Level, x: Int, y: Int, e: Entity
): Boolean = e is AirWizard

fun CloudCactusTile.hurt(
    level: Level, x: Int, y: Int, source: Mob, dmg: Int, attackDir: Int
) {
    hurt(level, x, y, 0)
}

fun CloudCactusTile.interact(
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

fun CloudCactusTile.hurt(level: Level, x: Int, y: Int, dmg: Int) {
    val damage = level.getData(x, y) + 1
    level.add(SmashParticle(x * 16 + 8, y * 16 + 8))
    level.add(
        TextParticle(
            "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)
        )
    )
    if (dmg > 0) {
        if (damage >= 10) {
            level.setTile(x, y, Tile.cloud, 0)
        } else {
            level.setData(x, y, damage)
        }
    }
}

fun CloudCactusTile.bumpedInto(level: Level, x: Int, y: Int, entity: Entity) {
    if (entity is AirWizard) return
    entity.hurt(this, x, y, 3)
}

