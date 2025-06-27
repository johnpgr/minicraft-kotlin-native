package entity

import gfx.*
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class Spark(
    val owner: AirWizard,
    var xa: Double,
    var ya: Double,
    override var x: Int = owner.x,
    override var y: Int = owner.y,
    var xx: Double = x.toDouble(),
    var yy: Double = y.toDouble(),
    var time: Int = 0,
    override var xr: Int = 0,
    override var yr: Int = 0,
    override var removed: Boolean = false,
) : Entity {
    override lateinit var level: Level
    override val random: Random = uniqueRandom()
    val lifeTime: Int = 60 * 10 + random.nextInt(30)
}

fun Spark.tick() {
    time++
    if (time >= lifeTime) {
        remove()
        return
    }

    xx += xa
    yy += ya
    x = xx.toInt()
    y = yy.toInt()

    val toHit = level.getEntities(x, y, x, y)
    toHit.forEach { ent ->
        if (ent is Mob && ent !is AirWizard) {
            ent.hurt(owner, 1, ent.dir xor 1)
        }
    }
}

fun Spark.isBlockableBy(mob: Mob): Boolean {
    return false
}

fun Spark.render(screen: Screen) {
    if (time >= lifeTime - 6 * 20) {
        if (time / 6 % 2 == 0) return
    }

    val xt = 8
    val yt = 13

    screen.render(
        x - 4,
        y - 4 - 2,
        xt + yt * 32,
        Color.get(-1, 555, 555, 555),
        random.nextInt(4)
    )
    screen.render(
        x - 4,
        y - 4 + 2,
        xt + yt * 32,
        Color.get(-1, 0, 0, 0),
        random.nextInt(4)
    )
}
