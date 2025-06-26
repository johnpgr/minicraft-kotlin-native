package level.tile

import entity.*
import gfx.*
import item.*
import item.resource.*
import level.*
import level.tile.renderer.GrassRenderer
import sound.*
import util.uniqueRandom
import kotlin.random.Random

data class GrassTile(
    override val id: Byte,
    override val random: Random = uniqueRandom(),
    val grassRenderer: GrassRenderer = GrassRenderer(random),
    override var connectsToGrass: Boolean = true,
    override var connectsToSand: Boolean = false,
    override var connectsToLava: Boolean = false,
    override var connectsToWater: Boolean = false,
) : Tile {
    init {
        Tile.tiles.add(this)
    }
}

fun GrassTile.render(screen: Screen, level: Level, x: Int, y: Int) =
    grassRenderer.render(screen, level, x, y)

fun GrassTile.tick(level: Level, xt: Int, yt: Int) {
    if (random.nextInt(40) != 0) return

    var xn = xt
    var yn = yt

    if (random.nextBoolean()) xn += random.nextInt(2) * 2 - 1
    else yn += random.nextInt(2) * 2 - 1

    if (level.getTile(xn, yn) == Tile.dirt) {
        level.setTile(xn, yn, this, 0)
    }
}

fun GrassTile.interact(
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    item: Item,
    attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.shovel) {
            if (player.payStamina(4 - item.level)) {
                level.setTile(xt, yt, Tile.dirt, 0)
                Sound.monsterHurt.play()
                if (random.nextInt(5) == 0) {
                    level.add(
                        ItemEntity(
                            ResourceItem(Resource.seeds),
                            xt * 16 + random.nextInt(10) + 3,
                            yt * 16 + random.nextInt(10) + 3
                        )
                    )
                    return true
                }
            }
        }
        if (item.type == ToolType.hoe) {
            if (player.payStamina(4 - item.level)) {
                Sound.monsterHurt.play()
                if (random.nextInt(5) == 0) {
                    level.add(
                        ItemEntity(
                            ResourceItem(Resource.seeds),
                            xt * 16 + random.nextInt(10) + 3,
                            yt * 16 + random.nextInt(10) + 3
                        )
                    )
                    return true
                }
                level.setTile(xt, yt, Tile.farmland, 0)
                return true
            }
        }
    }
    return false
}
