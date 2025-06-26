package screen

import entity.*
import game.Game
import gfx.*
import item.renderInventory

data class ContainerMenu(
    val player: Player,
    val title: String,
    val container: Inventory,
    var selected: Int = 0,
    var oSelected: Int = 0,
    var window: Int = 0,
) : Menu

fun ContainerMenu.tick() {
    if (_root_ide_package_.input.InputHandler.menu.clicked) Game.menu = null

    if (_root_ide_package_.input.InputHandler.left.clicked) {
        window = 0
        val tmp = selected
        selected = oSelected
        oSelected = tmp
    }
    if (_root_ide_package_.input.InputHandler.right.clicked) {
        window = 1
        val tmp = selected
        selected = oSelected
        oSelected = tmp
    }

    val i: Inventory = if (window == 1) player.inventory else container
    val i2: Inventory = if (window == 0) player.inventory else container

    val len: Int = i.items.size
    if (selected < 0) selected = 0
    if (selected >= len) selected = len - 1

    if (_root_ide_package_.input.InputHandler.up.clicked) selected--
    if (_root_ide_package_.input.InputHandler.down.clicked) selected++

    if (len == 0) selected = 0
    if (selected < 0) selected += len
    if (selected >= len) selected -= len

    if (_root_ide_package_.input.InputHandler.attack.clicked && len > 0) {
        i2.add(oSelected, i.items.removeAt(selected))
        if (selected >= i.items.size) selected = i.items.size - 1
    }
}

fun ContainerMenu.render(screen: Screen) {
    if (window == 1) {
        screen.xOffset = 6 * 8
        screen.yOffset = 0
    }

    Font.renderFrame(screen, title, 1, 1, 12, 11)
    renderItemList(
        screen,
        1,
        1,
        12,
        11,
        container.items,
        if (window == 0) selected else -oSelected - 1
    ) { screen, x, y ->
        renderInventory(screen, x, y)
    }

    Font.renderFrame(screen, "inventory", 13, 1, 13 + 11, 11)
    renderItemList(
        screen,
        13,
        1,
        13 + 11,
        11,
        player.inventory.items,
        if (window == 1) selected else -oSelected - 1
    ) { screen, x, y ->
        renderInventory(screen, x, y)
    }
    screen.xOffset = 0
    screen.yOffset = 0
}
