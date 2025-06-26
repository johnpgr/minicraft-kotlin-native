package level.tile

import entity.ItemEntity
import entity.Mob
import entity.Player
import entity.payStamina
import gfx.*
import item.Item
import item.ResourceItem
import item.ToolItem
import item.ToolType
import item.resource.Resource
import level.*
import level.tile.renderer.GrassRenderer
import util.uniqueRandom
import kotlin.random.Random

data class FlowerTile(
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

fun FlowerTile.render(screen: Screen, level: Level, x: Int, y: Int) {
    grassRenderer.render(screen, level, x, y)

    val data: Int = level.getData(x, y)
    val shape = (data / 16) % 2
    val flowerCol: Int = Color.get(10, level.grassColor, 555, 440)

    if (shape == 0) screen.render(
        x * 16 + 0,
        y * 16 + 0,
        1 + 1 * 32,
        flowerCol,
        0
    )
    if (shape == 1) screen.render(
        x * 16 + 8,
        y * 16 + 0,
        1 + 1 * 32,
        flowerCol,
        0
    )
    if (shape == 1) screen.render(
        x * 16 + 0,
        y * 16 + 8,
        1 + 1 * 32,
        flowerCol,
        0
    )
    if (shape == 0) screen.render(
        x * 16 + 8,
        y * 16 + 8,
        1 + 1 * 32,
        flowerCol,
        0
    )
}

fun FlowerTile.interact(
    level: Level,
    x: Int,
    y: Int,
    player: Player,
    item: Item,
    attackDir: Int
): Boolean {
    if (item is ToolItem) {
        if (item.type == ToolType.shovel) {
            if (player.payStamina(4 - item.level)) {
                level.add(
                    ItemEntity(
                        ResourceItem(Resource.flower),
                        x * 16 + random.nextInt(10) + 3,
                        y * 16 + random.nextInt(10) + 3
                    )
                )
                level.add(
                    ItemEntity(
                        ResourceItem(Resource.flower),
                        x * 16 + random.nextInt(10) + 3,
                        y * 16 + random.nextInt(10) + 3
                    )
                )
                level.setTile(x, y, Tile.grass, 0)
                return true
            }
        }
    }
    return false
}

fun FlowerTile.hurt(
    level: Level,
    x: Int,
    y: Int,
    source: Mob,
    dmg: Int,
    attackDir: Int
) {
    val count = random.nextInt(2) + 1
    for (i in 0..<count) {
        level.add(
            ItemEntity(
                ResourceItem(Resource.flower),
                x * 16 + random.nextInt(10) + 3,
                y * 16 + random.nextInt(10) + 3
            )
        )
    }
    level.setTile(x, y, Tile.grass, 0)
}
