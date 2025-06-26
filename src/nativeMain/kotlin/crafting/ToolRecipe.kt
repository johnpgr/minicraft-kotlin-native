package crafting

import entity.*
import item.*

data class ToolRecipe(
    val type: ToolType, val level: Int,
    override var costs: MutableList<Item> = mutableListOf(),
    override var canCraft: Boolean = false,
    override var resultTemplate: Item = ToolItem(type, level)
) : Recipe

fun ToolRecipe.craft(player: Player) {
    player.inventory.add(0, ToolItem(type, level))
}
