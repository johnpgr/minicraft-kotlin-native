package screen

import input.*
import game.*
import gfx.*
import sound.*

data class TitleMenu(var selected: Int = 0) : Menu {
    companion object {
        val options =
            arrayOf<String>("Start game", "How to play", "About")
    }
}

fun TitleMenu.tick() {
    if (InputHandler.up.clicked) selected--
    if (InputHandler.down.clicked) selected++

    val len = TitleMenu.options.size
    if (selected < 0) selected += len
    if (selected >= len) selected -= len

    if (InputHandler.attack.clicked || InputHandler.menu.clicked) {
        if (selected == 0) {
            Sound.test.play()
            Game.resetGame()
            Game.menu = null
        }
        if (selected == 1) Game.menu = InstructionsMenu(this)
        if (selected == 2) Game.menu = AboutMenu(this)
    }
}

fun TitleMenu.render(screen: Screen) {
    screen.clear(0)

    val h = 2
    val w = 13
    val titleColor: Int = Color.get(0, 8, 131, 551)
    val xo: Int = (screen.w - w * 8) / 2
    val yo = 24
    for (y in 0..<h) {
        for (x in 0..<w) {
            screen.render(
                xo + x * 8,
                yo + y * 8,
                x + (y + 6) * 32,
                titleColor,
                0
            )
        }
    }

    for (i in 0..2) {
        var msg = TitleMenu.options[i]
        var col: Int = Color.get(0, 222, 222, 222)
        if (i == selected) {
            msg = "> " + msg + " <"
            col = Color.get(0, 555, 555, 555)
        }
        Font.draw(
            msg,
            screen,
            (screen.w - msg.length * 8) / 2,
            (8 + i) * 8,
            col
        )
    }

    Font.draw(
        "(Arrow keys,X and C)",
        screen,
        0,
        screen.h - 8,
        Color.get(0, 111, 111, 111)
    )
}
