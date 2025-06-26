package item

import gfx.*
import entity.*
import item.resource.*
import level.*
import level.tile.*

data class ResourceItem(
    val resource: Resource,
    var count: Int = 1,
) : Item {
    override val color: Int
        get() = resource.color
    override val sprite: Int
        get() = resource.sprite
    override val name: String
        get() = resource.name
}

fun ResourceItem.renderIcon(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, resource.sprite, resource.color, 0)
}

fun ResourceItem.renderInventory(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, resource.sprite, resource.color, 0)
    Font.draw(
        resource.name, screen, x + 32, y, Color.get(-1, 555, 555, 555)
    )
    var cc = count
    if (cc > 999) cc = 999
    Font.draw("" + cc, screen, x + 8, y, Color.get(-1, 444, 444, 444))
}

fun ResourceItem.onTake(itemEntity: ItemEntity) {
}

fun ResourceItem.interactOn(
    tile: Tile, level: Level, xt: Int, yt: Int, player: Player, attackDir: Int
): Boolean {
    if (resource.interactOn(tile, level, xt, yt, player, attackDir)) {
        count--
        return true
    }
    return false
}

val ResourceItem.isDepleted: Boolean
    get() = count <= 0
