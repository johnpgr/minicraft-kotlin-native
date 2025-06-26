package level.tile.renderer

import gfx.*
import level.*
import kotlin.random.Random

class GrassRenderer(val random: Random) {
    fun render(screen: Screen, level: Level, x: Int, y: Int) {
        val col = Color.get(
            level.grassColor,
            level.grassColor,
            level.grassColor + 111,
            level.grassColor + 111
        )
        val transitionColor =
            Color.get(
                level.grassColor - 111,
                level.grassColor,
                level.grassColor + 111,
                level.dirtColor
            )

        val u = !(level.getTile(x, y - 1)?.connectsToGrass ?: false)
        val d = !(level.getTile(x, y + 1)?.connectsToGrass ?: false)
        val l = !(level.getTile(x - 1, y)?.connectsToGrass ?: false)
        val r = !(level.getTile(x + 1, y)?.connectsToGrass ?: false)

        if (!u && !l) screen.render(x * 16, y * 16, 0, col, 0)
        else screen.render(
            x * 16,
            y * 16,
            (if (l) 11 else 12) + (if (u) 0 else 1) * 32,
            transitionColor,
            0
        )

        if (!u && !r) screen.render(x * 16 + 8, y * 16, 1, col, 0)
        else screen.render(
            x * 16 + 8,
            y * 16,
            (if (r) 13 else 12) + (if (u) 0 else 1) * 32,
            transitionColor,
            0
        )

        if (!d && !l) screen.render(x * 16, y * 16 + 8, 2, col, 0)
        else screen.render(
            x * 16,
            y * 16 + 8,
            (if (l) 11 else 12) + (if (d) 2 else 1) * 32,
            transitionColor,
            0
        )

        if (!d && !r) screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0)
        else screen.render(
            x * 16 + 8,
            y * 16 + 8,
            (if (r) 13 else 12) + (if (d) 2 else 1) * 32,
            transitionColor,
            0
        )
    }
}