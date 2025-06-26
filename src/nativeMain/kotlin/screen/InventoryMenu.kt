package screen

import entity.*
import game.Game
import gfx.*
import item.renderInventory

data class InventoryMenu(val player: Player, var selected: Int = 0) : Menu {
    init {
        player.activeItem?.let { item ->
            player.inventory.items.add(0, item)
            player.activeItem = null
        }
    }
}

fun InventoryMenu.tick() {
    if (_root_ide_package_.input.InputHandler.menu.clicked) Game.menu = null

    if (_root_ide_package_.input.InputHandler.up.clicked) selected--
    if (_root_ide_package_.input.InputHandler.down.clicked) selected++

    val len: Int = player.inventory.items.size
    if (len == 0) selected = 0
    if (selected < 0) selected += len
    if (selected >= len) selected -= len

    if (_root_ide_package_.input.InputHandler.attack.clicked && len > 0) {
        val item = player.inventory.items.removeAt(selected)
        player.activeItem = item
        Game.menu = null
    }
}

fun InventoryMenu.render(screen: Screen) {
    Font.renderFrame(screen, "inventory", 1, 1, 12, 11)
    renderItemList(
        screen, 1, 1, 12, 11, player.inventory.items, selected
    ) { screen, x, y ->
        renderInventory(screen, x, y)
    }
}
