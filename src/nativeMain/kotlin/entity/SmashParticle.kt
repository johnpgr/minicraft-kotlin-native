package entity

import gfx.*
import level.Level
import sound.Sound
import sound.play
import kotlin.random.Random

data class SmashParticle(
    override var x: Int,
    override var y: Int,
    override var xr: Int = 6,
    override var yr: Int = 6,
    override var removed: Boolean = false,
    var time: Int = 0,
) : Entity {
    override val random: Random = util.uniqueRandom()
    override lateinit var level: Level

    init {
        Sound.monsterHurt.play()
    }
}

fun SmashParticle.tick() {
    time++
    if (time > 10) {
        remove()
    }
}

fun SmashParticle.render(screen: Screen) {
    val col: Int = Color.get(-1, 555, 555, 555)
    screen.render(x - 8, y - 8, 5 + 12 * 32, col, 2)
    screen.render(x - 0, y - 8, 5 + 12 * 32, col, 3)
    screen.render(x - 8, y - 0, 5 + 12 * 32, col, 0)
    screen.render(x - 0, y - 0, 5 + 12 * 32, col, 1)
}
