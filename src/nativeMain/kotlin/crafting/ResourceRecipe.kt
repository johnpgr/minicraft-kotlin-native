package crafting

import entity.Player
import entity.add
import item.Item
import item.ResourceItem
import item.resource.Resource

data class ResourceRecipe(
    val resource: Resource,
    override var costs: MutableList<Item> = mutableListOf(),
    override var canCraft: Boolean = false,
    override var resultTemplate: Item = ResourceItem(resource, 1),
) : Recipe

fun ResourceRecipe.craft(player: Player) {
    player.inventory.add(0, ResourceItem(resource, 1))
}