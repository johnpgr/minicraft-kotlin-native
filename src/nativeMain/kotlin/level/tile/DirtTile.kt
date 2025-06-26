package level.tile

import kotlin.random.Random
import entity.*
import gfx.*
import item.Item
import item.ResourceItem
import item.ToolItem
import item.ToolType
import item.resource.Resource
import level.*
import sound.Sound
import sound.play

data class DirtTile(
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

fun DirtTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    val col: Int = Color.get(
        level.dirtColor,
        level.dirtColor,
        level.dirtColor - 111,
        level.dirtColor - 111
    )
    screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0)
    screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0)
    screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0)
    screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0)
}

fun DirtTile.interact(
    level: Level, xt: Int, yt: Int, player: Player, item: Item, attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.shovel) {
            if (player.payStamina(4 - item.level)) {
                level.setTile(xt, yt, Tile.hole, 0)
                level.add(
                    ItemEntity(
                        ResourceItem(Resource.dirt),
                        xt * 16 + random.nextInt(10) + 3,
                        yt * 16 + random.nextInt(10) + 3
                    )
                )
                Sound.monsterHurt.play()
                return true
            }
        }
        if (item.type == ToolType.hoe) {
            if (player.payStamina(4 - item.level)) {
                level.setTile(xt, yt, Tile.farmland, 0)
                Sound.monsterHurt.play()
                return true
            }
        }
    }
    return false
}
