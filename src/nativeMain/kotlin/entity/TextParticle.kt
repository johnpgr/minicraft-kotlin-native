package entity

import gfx.*
import level.Level
import util.nextGaussian
import util.uniqueRandom
import kotlin.math.*
import kotlin.random.Random

data class TextParticle(
    val msg: String,
    override var x: Int,
    override var y: Int,
    val color: Int,
    override var xr: Int = 6,
    override var yr: Int = 6,
    override var removed: Boolean = false,
    var time: Int = 0,
    var xa: Double = 0.0,
    var ya: Double = 0.0,
    var za: Double = 0.0,
    var xx: Double = x.toDouble(),
    var yy: Double = y.toDouble(),
    var zz: Double = 2.0,
) : Entity {
    override val random: Random = uniqueRandom()
    override lateinit var level: Level

    init {
        xa = random.nextGaussian() * 0.3
        ya = random.nextGaussian() * 0.2
        za = random.nextFloat() * 0.7 + 2
    }
}

fun TextParticle.tick() {
    time++
    if (time > 60) {
        remove()
    }
    xx += xa
    yy += ya
    zz += za
    if (zz < 0) {
        zz = 0.0
        za *= -0.5
        xa *= 0.6
        ya *= 0.6
    }
    za -= 0.15
    x = xx.toInt()
    y = yy.toInt()
}

fun TextParticle.render(screen: Screen) {
    Font.draw(
        msg,
        screen,
        x - msg.length * 4 + 1,
        y - (zz).toInt() + 1,
        Color.get(-1, 0, 0, 0)
    )
    Font.draw(msg, screen, x - msg.length * 4, y - (zz).toInt(), color)
}