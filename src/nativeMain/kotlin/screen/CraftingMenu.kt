package screen

import crafting.*
import entity.*
import gfx.*
import item.*
import sound.Sound
import sound.play

data class CraftingMenu(
    val recipes: MutableList<Recipe>, val player: Player, var selected: Int = 0
) : Menu {
    init {
        recipes.forEach { recipe ->
            recipe.checkCanCraft(player)
        }

        this.recipes.sortWith { r1, r2 ->
            when {
                r1.canCraft && !r2.canCraft -> -1
                !r1.canCraft && r2.canCraft -> 1
                else -> 0
            }
        }
    }
}

fun CraftingMenu.tick() {
    if (InputHandler.menu.clicked) Game.menu = null

    if (InputHandler.up.clicked) selected--
    if (InputHandler.down.clicked) selected++

    val len: Int = recipes.size
    if (len == 0) selected = 0
    if (selected < 0) selected += len
    if (selected >= len) selected -= len

    if (InputHandler.attack.clicked && len > 0) {
        val r = recipes[selected]
        r.checkCanCraft(player)
        if (r.canCraft) {
            r.deductCost(player)
            r.craft(player)
            Sound.craft.play()
        }
        for (i in 0..<recipes.size) {
            recipes.get(i).checkCanCraft(player)
        }
    }
}

fun CraftingMenu.render(screen: Screen) {
    Font.renderFrame(screen, "Have", 12, 1, 19, 3)
    Font.renderFrame(screen, "Cost", 12, 4, 19, 11)
    Font.renderFrame(screen, "Crafting", 0, 1, 11, 11)
    renderItemList(screen, 0, 1, 11, 11, recipes, selected) { screen, x, y ->
        renderInventory(screen, x, y)
    }

    if (recipes.size > 0) {
        val recipe = recipes[selected]
        val hasResultItems = player.inventory.count(recipe.resultTemplate)
        val xo = 13 * 8
        screen.render(
            xo,
            2 * 8,
            recipe.resultTemplate.sprite,
            recipe.resultTemplate.color,
            0
        )
        Font.draw(
            "" + hasResultItems,
            screen,
            xo + 8,
            2 * 8,
            Color.get(-1, 555, 555, 555)
        )

        val costs: List<Item> = recipe.costs
        costs.forEachIndexed { i, item ->
            val yo = (5 + i) * 8
            screen.render(xo, yo, item.sprite, item.color, 0)
            var requiredAmt = 1
            if (item is ResourceItem) {
                requiredAmt = item.count
            }
            var has: Int = player.inventory.count(item)
            var color: Int = Color.get(-1, 555, 555, 555)
            if (has < requiredAmt) {
                color = Color.get(-1, 222, 222, 222)
            }
            if (has > 99) has = 99
            Font.draw(
                "" + requiredAmt + "/" + has, screen, xo + 8, yo, color
            )
        }
    }
    // renderItemList(screen, 12, 4, 19, 11, recipes.get(selected).costs, -1);
}
