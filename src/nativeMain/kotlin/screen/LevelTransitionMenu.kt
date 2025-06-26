package screen

import changeLevel
import gfx.*

data class LevelTransitionMenu(val dir: Int, var time: Int = 0) : Menu

fun LevelTransitionMenu.tick() {
    time += 2
    if (time == 30) Game.changeLevel(dir)
    if (time == 60) Game.menu = null
}

fun LevelTransitionMenu.render(screen: Screen) {
    for (x in 0..19) {
        for (y in 0..14) {
            val dd = (y + x % 2 * 2 + x / 3) - time
            if (dd < 0 && dd > -30) {
                if (dir > 0) screen.render(x * 8, y * 8, 0, 0, 0)
                else screen.render(x * 8, screen.h - y * 8 - 8, 0, 0, 0)
            }
        }
    }
}
