package item

import gfx.*
import entity.*
import kotlin.random.Random

data class ToolItem(
    val type: ToolType,
    var level: Int,
    val random: Random = util.uniqueRandom(),
) : Item {
    override val color: Int
        get() = LEVEL_COLORS[level]

    override val sprite: Int
        get() = type.sprite + 5 * 32

    override val name: String
        get() = LEVEL_NAMES[level] + " " + type.name

    companion object {
        const val MAX_LEVEL = 5
        val LEVEL_NAMES = arrayOf<String>("Wood", "Rock", "Iron", "Gold", "Gem")

        val LEVEL_COLORS = intArrayOf(
            Color.get(-1, 100, 321, 431),
            Color.get(-1, 100, 321, 111),
            Color.get(-1, 100, 321, 555),
            Color.get(-1, 100, 321, 550),
            Color.get(-1, 100, 321, 45),
        )
    }
}

fun ToolItem.renderIcon(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, this.sprite, this.color, 0)
}

fun ToolItem.renderInventory(screen: Screen, x: Int, y: Int) {
    screen.render(x, y, this.sprite, this.color, 0)
    Font.draw(this.name, screen, x + 8, y, Color.get(-1, 555, 555, 555))
}


fun ToolItem.onTake(itemEntity: ItemEntity) {
}

val ToolItem.canAttack: Boolean get() = true

fun ToolItem.getAttackDamageBonus(e: Entity): Int {
    if (type === ToolType.axe) {
        return (level + 1) * 2 + random.nextInt(4)
    }
    if (type === ToolType.sword) {
        return (level + 1) * 3 + random.nextInt(2 + level * level * 2)
    }
    return 1
}

fun ToolItem.matches(item: Item): Boolean {
    if (item is ToolItem) {
        val other = item
        if (other.type !== type) return false
        if (other.level != level) return false
        return true
    }
    return false
}
