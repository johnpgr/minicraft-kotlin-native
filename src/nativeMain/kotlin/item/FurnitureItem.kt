package item

import gfx.*
import level.tile.*
import level.*
import entity.*

data class FurnitureItem(
    val furniture: Furniture,
    var placed: Boolean = false,
) : Item {
    override val color: Int
        get() = furniture.color
    override val sprite: Int
        get() = furniture.sprite + 10 * 32
    override val name: String
        get() = furniture.name
}

fun FurnitureItem.renderIcon(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, sprite, color, 0)
}

fun FurnitureItem.renderInventory(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, sprite, color, 0)
    Font.draw(
        furniture.name,
        screen,
        x + 8,
        y,
        Color.get(-1, 555, 555, 555)
    )
}

fun FurnitureItem.onTake(itemEntity: ItemEntity) {
}

fun FurnitureItem.canAttack(): Boolean {
    return false
}

fun FurnitureItem.interactOn(
    tile: Tile,
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    attackDir: Int
): Boolean {
    if (tile.mayPass(level, xt, yt, furniture)) {
        furniture.x = xt * 16 + 8
        furniture.y = yt * 16 + 8
        level.add(furniture)
        placed = true
        return true
    }
    return false
}

val FurnitureItem.isDepleted: Boolean
    get() = placed