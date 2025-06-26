package crafting

import entity.Player
import entity.hasResources
import entity.removeResource
import gfx.Color
import gfx.Font
import gfx.Screen
import gfx.draw
import gfx.render
import item.*
import item.resource.Resource

sealed interface Recipe {
    var costs: MutableList<Item>
    var canCraft: Boolean
    var resultTemplate: Item
}

fun Recipe.addCost(resource: Resource, count: Int): Recipe {
    costs.add(ResourceItem(resource, count))
    return this
}

fun Recipe.checkCanCraft(player: Player) {
    costs.forEach { item ->
        if (item is ResourceItem) {
            val ri = item
            if (!player.inventory.hasResources(ri.resource, ri.count)) {
                canCraft = false
                return
            }
        }
    }
    canCraft = true
}

fun Recipe.renderInventory(screen: Screen, x: Int, y: Int) {
    screen.render(
        x,
        y,
        resultTemplate.sprite,
        resultTemplate.color,
        0
    )
    val textColor: Int =
        if (canCraft) Color.get(-1, 555, 555, 555) else Color.get(
            -1,
            222,
            222,
            222
        )
    Font.draw(resultTemplate.name, screen, x + 8, y, textColor)
}

fun Recipe.craft(player: Player) {}

fun Recipe.deductCost(player: Player) {
    costs.forEach { item ->
        if (item is ResourceItem) {
            player.inventory.removeResource(item.resource, item.count)
        }
    }
}
