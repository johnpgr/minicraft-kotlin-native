package item

import entity.Entity
import entity.Furniture
import entity.Player
import entity.take
import gfx.*

data class PowerGloveItem(
    override val color: Int = Color.get(-1, 100, 320, 430),
    override val sprite: Int = 7 + 4 * 32,
    override val name: String = "Pow glove",
) : Item

fun PowerGloveItem.renderIcon(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, sprite, color, 0)
}

fun PowerGloveItem.renderInventory(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, sprite, color, 0)
    Font.draw(name, screen, x + 8, y, Color.get(-1, 555, 555, 555))
}

fun PowerGloveItem.interact(
    player: Player,
    entity: Entity,
    attackDir: Int
): Boolean {
    if (entity is Furniture) {
        entity.take(player)
        return true
    }
    return false
}
