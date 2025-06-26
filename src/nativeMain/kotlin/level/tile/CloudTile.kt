package level.tile

import entity.*
import gfx.*
import item.Item
import item.ResourceItem
import item.ToolItem
import item.ToolType
import item.resource.Resource
import level.*
import kotlin.random.Random

data class CloudTile(
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

fun CloudTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col = Color.get(444, 444, 555, 555)
    val transitionColor = Color.get(333, 444, 555, -1)

    val u = level.getTile(x, y - 1) == Tile.infiniteFall
    val d = level.getTile(x, y + 1) == Tile.infiniteFall
    val l = level.getTile(x - 1, y) == Tile.infiniteFall
    val r = level.getTile(x + 1, y) == Tile.infiniteFall

    val ul = level.getTile(x - 1, y - 1) == Tile.infiniteFall
    val dl = level.getTile(x - 1, y + 1) == Tile.infiniteFall
    val ur = level.getTile(x + 1, y - 1) == Tile.infiniteFall
    val dr = level.getTile(x + 1, y + 1) == Tile.infiniteFall

    if (!u && !l) {
        if (!ul) screen.render(x * 16 + 0, y * 16 + 0, 17, col, 0)
        else screen.render(
            x * 16 + 0, y * 16 + 0, 7 + 0 * 32, transitionColor, 3
        )
    } else screen.render(
        x * 16 + 0,
        y * 16 + 0,
        (if (l) 6 else 5) + (if (u) 2 else 1) * 32,
        transitionColor,
        3
    )

    if (!u && !r) {
        if (!ur) screen.render(x * 16 + 8, y * 16 + 0, 18, col, 0)
        else screen.render(
            x * 16 + 8, y * 16 + 0, 8 + 0 * 32, transitionColor, 3
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 0,
        (if (r) 4 else 5) + (if (u) 2 else 1) * 32,
        transitionColor,
        3
    )

    if (!d && !l) {
        if (!dl) screen.render(x * 16 + 0, y * 16 + 8, 20, col, 0)
        else screen.render(
            x * 16 + 0, y * 16 + 8, 7 + 1 * 32, transitionColor, 3
        )
    } else screen.render(
        x * 16 + 0,
        y * 16 + 8,
        (if (l) 6 else 5) + (if (d) 0 else 1) * 32,
        transitionColor,
        3
    )
    if (!d && !r) {
        if (!dr) screen.render(x * 16 + 8, y * 16 + 8, 19, col, 0)
        else screen.render(
            x * 16 + 8, y * 16 + 8, 8 + 1 * 32, transitionColor, 3
        )
    } else screen.render(
        x * 16 + 8,
        y * 16 + 8,
        (if (r) 4 else 5) + (if (d) 0 else 1) * 32,
        transitionColor,
        3
    )
}

fun CloudTile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean {
    return true
}

fun CloudTile.interact(
    level: Level, xt: Int, yt: Int, player: Player, item: Item, attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.shovel) {
            if (player.payStamina(5)) {
                // level.setTile(xt, yt, Tile.infiniteFall, 0);
                val count: Int = random.nextInt(2) + 1
                for (i in 0..<count) {
                    level.add(
                        ItemEntity(
                            ResourceItem(Resource.cloud),
                            xt * 16 + random.nextInt(10) + 3,
                            yt * 16 + random.nextInt(10) + 3
                        )
                    )
                }
                return true
            }
        }
    }
    return false
}
