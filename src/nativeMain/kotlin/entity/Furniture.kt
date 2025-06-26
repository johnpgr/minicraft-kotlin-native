package entity

import gfx.*
import item.FurnitureItem
import item.PowerGloveItem
import level.Level
import kotlin.random.Random

sealed interface Furniture : Entity {
    var pushTime: Int
    var pushDir: Int
    val color: Int
    val sprite: Int
    val name: String
    var shouldTake: Player?
}

fun Furniture.tick() {
    shouldTake?.let { player ->
        player.activeItem?.let { item ->
            if (item is PowerGloveItem) {
                remove()
                player.inventory.add(0, item)
                player.activeItem = FurnitureItem(this)
            }
        }

        shouldTake = null
    }
    if (pushDir == 0) move(0, +1)
    if (pushDir == 1) move(0, -1)
    if (pushDir == 2) move(-1, 0)
    if (pushDir == 3) move(+1, 0)
    pushDir = -1
    if (pushTime > 0) pushTime--
}

fun Furniture.render(screen: Screen) {
    screen.render(x - 8, y - 8 - 4, sprite * 2 + 8 * 32, color, 0)
    screen.render(x - 0, y - 8 - 4, sprite * 2 + 8 * 32 + 1, color, 0)
    screen.render(x - 8, y - 0 - 4, sprite * 2 + 8 * 32 + 32, color, 0)
    screen.render(x - 0, y - 0 - 4, sprite * 2 + 8 * 32 + 33, color, 0)
}

fun Furniture.blocks(e: Entity): Boolean {
    return true
}

fun Furniture.touchedBy(entity: Entity) {
    if (entity is Player && pushTime == 0) {
        pushDir = entity.dir
        pushTime = 10
    }
}

fun Furniture.take(player: Player) {
    shouldTake = player
}
