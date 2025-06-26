package level.tile

import entity.*
import gfx.*
import level.*
import item.*
import item.resource.Resource
import util.uniqueRandom
import kotlin.random.Random

data class RockTile(
    override val id: Byte,
    override val random: Random = uniqueRandom(),
    override var connectsToGrass: Boolean = false,
    override var connectsToSand: Boolean = false,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun RockTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col: Int = Color.get(444, 444, 333, 333)
    val transitionColor: Int = Color.get(111, 444, 555, level.dirtColor)

    val u = level.getTile(x, y - 1) != this
    val d = level.getTile(x, y + 1) != this
    val l = level.getTile(x - 1, y) != this
    val r = level.getTile(x + 1, y) != this

    val ul = level.getTile(x - 1, y - 1) != this
    val dl = level.getTile(x - 1, y + 1) != this
    val ur = level.getTile(x + 1, y - 1) != this
    val dr = level.getTile(x + 1, y + 1) != this

    if (!u && !l) {
        if (!ul) screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0)
        else screen.render(
            x * 16 + 0,
            y * 16 + 0,
            7 + 0 * 32,
            transitionColor,
            3
        )
    } else screen.render(
        x * 16 + 0,
        y * 16 + 0,
        (if (l) 6 else 5) + (if (u) 2 else 1) * 32,
        transitionColor,
        3
    )

    if (!u && !r) {
        if (!ur) screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0)
        else screen.render(
            x * 16 + 8,
            y * 16 + 0,
            8 + 0 * 32,
            transitionColor,
            3
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 0,
        (if (r) 4 else 5) + (if (u) 2 else 1) * 32,
        transitionColor,
        3
    )

    if (!d && !l) {
        if (!dl) screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0)
        else screen.render(
            x * 16 + 0,
            y * 16 + 8,
            7 + 1 * 32,
            transitionColor,
            3
        )
    } else screen.render(
        x * 16 + 0,
        y * 16 + 8,
        (if (l) 6 else 5) + (if (d) 0 else 1) * 32,
        transitionColor,
        3
    )
    if (!d && !r) {
        if (!dr) screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0)
        else screen.render(
            x * 16 + 8,
            y * 16 + 8,
            8 + 1 * 32,
            transitionColor,
            3
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 8,
        (if (r) 4 else 5) + (if (d) 0 else 1) * 32,
        transitionColor,
        3
    )
}

fun RockTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean {
    return false
}

fun RockTile.hurt(
    level: Level,
    x: Int,
    y: Int,
    source: Mob,
    dmg: Int,
    attackDir: Int
) {
    hurt(level, x, y, dmg)
}

fun RockTile.interact(
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    item: Item,
    attackDir: Int
): Boolean {
    if (item is ToolItem) {
        val tool: ToolItem = item as ToolItem
        if (tool.type == ToolType.pickaxe) {
            if (player.payStamina(4 - tool.level)) {
                hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10)
                return true
            }
        }
    }
    return false
}

fun RockTile.hurt(level: Level, x: Int, y: Int, dmg: Int) {
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
    if (damage >= 50) {
        var count: Int = random.nextInt(4) + 1
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(Resource.stone),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
        count = random.nextInt(2)
        for (i in 0..<count) {
            level.add(
                ItemEntity(
                    ResourceItem(Resource.coal),
                    x * 16 + random.nextInt(10) + 3,
                    y * 16 + random.nextInt(10) + 3
                )
            )
        }
        level.setTile(x, y, Tile.dirt, 0)
    } else {
        level.setData(x, y, damage)
    }
}

fun RockTile.tick(level: Level, xt: Int, yt: Int) {
    val damage: Int = level.getData(xt, yt)
    if (damage > 0) level.setData(xt, yt, damage - 1)
}
