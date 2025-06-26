package crafting

import entity.*
import item.*

data class FurnitureRecipe(
    val furnitureFactory: () -> Furniture,
    override var costs: MutableList<Item> = mutableListOf(),
    override var canCraft: Boolean = false,
    override var resultTemplate: Item = FurnitureItem(furnitureFactory())
): Recipe

fun FurnitureRecipe.craft(player: Player) {
    player.inventory.add(0, FurnitureItem(furnitureFactory()))
}
