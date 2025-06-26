package screen

import gfx.*

data class WonMenu(var inputDelay: Int = 60) : Menu {
}

fun WonMenu.tick() {
    if (inputDelay > 0) inputDelay--
    else if (InputHandler.attack.clicked || InputHandler.menu.clicked) {
        Game.menu = TitleMenu()
    }
}

fun WonMenu.render(screen: Screen) {
    Font.renderFrame(screen, "", 1, 3, 18, 9)
    Font.draw(
        "You won! Yay!",
        screen,
        2 * 8,
        4 * 8,
        Color.get(-1, 555, 555, 555)
    )

    var seconds: Int = Game.gameTime / 60
    var minutes = seconds / 60
    val hours = minutes / 60
    minutes %= 60
    seconds %= 60

    var timeString = ""
    if (hours > 0) {
        timeString =
            hours.toString() + "h" + (if (minutes < 10) "0" else "") + minutes + "m"
    } else {
        timeString =
            minutes.toString() + "m " + (if (seconds < 10) "0" else "") + seconds + "s"
    }
    Font.draw("Time:", screen, 2 * 8, 5 * 8, Color.get(-1, 555, 555, 555))
    Font.draw(
        timeString,
        screen,
        (2 + 5) * 8,
        5 * 8,
        Color.get(-1, 550, 550, 550)
    )
    Font.draw("Score:", screen, 2 * 8, 6 * 8, Color.get(-1, 555, 555, 555))
    Font.draw(
        "" + Game.player?.score,
        screen,
        (2 + 6) * 8,
        6 * 8,
        Color.get(-1, 550, 550, 550)
    )
    Font.draw(
        "Press C to win",
        screen,
        2 * 8,
        8 * 8,
        Color.get(-1, 333, 333, 333)
    )
}
