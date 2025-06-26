package level.tile

import kotlin.random.Random
import gfx.*
import level.*
import entity.*
import item.*
import item.resource.Resource
import util.uniqueRandom

data class TreeTile(
    override val id: Byte,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = true,
    override var connectsToSand: Boolean = false,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun TreeTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col: Int = Color.get(10, 30, 151, level.grassColor)
    val barkCol1: Int = Color.get(10, 30, 430, level.grassColor)
    val barkCol2: Int = Color.get(10, 30, 320, level.grassColor)

    val u = level.getTile(x, y - 1) == this
    val l = level.getTile(x - 1, y) == this
    val r = level.getTile(x + 1, y) == this
    val d = level.getTile(x, y + 1) == this
    val ul = level.getTile(x - 1, y - 1) == this
    val ur = level.getTile(x + 1, y - 1) == this
    val dl = level.getTile(x - 1, y + 1) == this
    val dr = level.getTile(x + 1, y + 1) == this

    if (u && ul && l) {
        screen.render(x * 16 + 0, y * 16 + 0, 10 + 1 * 32, col, 0)
    } else {
        screen.render(x * 16 + 0, y * 16 + 0, 9 + 0 * 32, col, 0)
    }
    if (u && ur && r) {
        screen.render(x * 16 + 8, y * 16 + 0, 10 + 2 * 32, barkCol2, 0)
    } else {
        screen.render(x * 16 + 8, y * 16 + 0, 10 + 0 * 32, col, 0)
    }
    if (d && dl && l) {
        screen.render(x * 16 + 0, y * 16 + 8, 10 + 2 * 32, barkCol2, 0)
    } else {
        screen.render(x * 16 + 0, y * 16 + 8, 9 + 1 * 32, barkCol1, 0)
    }
    if (d && dr && r) {
        screen.render(x * 16 + 8, y * 16 + 8, 10 + 1 * 32, col, 0)
    } else {
        screen.render(x * 16 + 8, y * 16 + 8, 10 + 3 * 32, barkCol2, 0)
    }
}

fun TreeTile.tick(level: Level, xt: Int, yt: Int) {
    val damage: Int = level.getData(xt, yt)
    if (damage > 0) level.setData(xt, yt, damage - 1)
}

fun TreeTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean = false

fun TreeTile.hurt(
    level: Level,
    x: Int,
    y: Int,
    source: Mob,
    dmg: Int,
    attackDir: Int
) {
    hurt(level, x, y, dmg)
}

fun TreeTile.interact(
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    item: Item,
    attackDir: Int
): Boolean {
    if (item is ToolItem) {
        val tool: ToolItem = item as ToolItem
        if (tool.type == ToolType.axe) {
            if (player.payStamina(4 - tool.level)) {
                hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10)
                return true
            }
        }
    }
    return false
}

fun TreeTile.hurt(level: Level, x: Int, y: Int, dmg: Int) {
    run {
        val count = if (random.nextInt(10) == 0) 1 else 0
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(Resource.apple),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
    }
    val damage: Int = level.getData(x, y) + dmg
    level.add(SmashParticle(x * 16 + 8, y * 16 + 8))
    level.add(
        TextParticle(
            "" + dmg,
            x * 16 + 8,
            y * 16 + 8,
            Color.get(-1, 500, 500, 500)
        )
    )
    if (damage >= 20) {
        var count = random.nextInt(2) + 1
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(Resource.wood),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
        count = random.nextInt(random.nextInt(4) + 1)
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(Resource.acorn),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
        level.setTile(x, y, Tile.grass, 0)
    } else {
        level.setData(x, y, damage)
    }
}
