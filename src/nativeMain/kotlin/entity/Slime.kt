package entity

import item.ResourceItem
import gfx.*
import item.resource.Resource
import level.*
import util.uniqueRandom
import kotlin.random.Random

data class Slime(
    var lvl: Int,
    override var x: Int = 0,
    override var y: Int = 0,
    var xa: Int = 0,
    var ya: Int = 0,
    var jumpTime: Int = 0,
    override var walkDist: Int = 0,
    override var dir: Int = 0,
    override var hurtTime: Int = 0,
    override var xKnockback: Int = 0,
    override var yKnockback: Int = 0,
    override var maxHealth: Int = lvl * lvl * 5,
    override var health: Int = maxHealth,
    override var swimTimer: Int = 0,
    override var tickTime: Int = 0,
    override var xr: Int = 6,
    override var yr: Int = 6,
    override var removed: Boolean = false,
) : Mob {
    override val random: Random = uniqueRandom()
    override lateinit var level: Level

    init {
        x = random.nextInt(64 * 16)
        y = random.nextInt(64 * 16)
    }
}

fun Slime.tick() {
    (this as Mob).tick()

    val speed = 1
    if (!move(xa * speed, ya * speed) || random.nextInt(40) == 0) {
        if (jumpTime <= -10) {
            xa = (random.nextInt(3) - 1)
            ya = (random.nextInt(3) - 1)
            level.player?.let { player ->
                val xd = player.x - x
                val yd = player.y - y
                if (xd * xd + yd * yd < 50 * 50) {
                    if (xd < 0) xa = -1
                    if (xd > 0) xa = +1
                    if (yd < 0) ya = -1
                    if (yd > 0) ya = +1
                }

            }

            if (xa != 0 || ya != 0) jumpTime = 10
        }
    }

    jumpTime--
    if (jumpTime == 0) {
        ya = 0
        xa = ya
    }
}

fun Slime.die() {
    (this as Mob).die()

    val count = random.nextInt(2) + 1
    for (i in 0..<count) {
        level.add(
            ItemEntity(
                ResourceItem(Resource.slime),
                x + random.nextInt(11) - 5,
                y + random.nextInt(11) - 5,
            )
        )
    }

    level.player?.let { player ->
        player.score += 25 * lvl
    }
}

fun Slime.render(screen: Screen) {
    var xt = 0
    val yt = 18

    val xo = x - 8
    var yo = y - 11

    if (jumpTime > 0) {
        xt += 2
        yo -= 4
    }

    var col: Int = Color.get(-1, 10, 252, 555)
    if (lvl == 2) col = Color.get(-1, 100, 522, 555)
    if (lvl == 3) col = Color.get(-1, 111, 444, 555)
    if (lvl == 4) col = Color.get(-1, 0, 111, 224)

    if (hurtTime > 0) {
        col = Color.get(-1, 555, 555, 555)
    }

    screen.render(xo + 0, yo + 0, xt + yt * 32, col, 0)
    screen.render(xo + 8, yo + 0, xt + 1 + yt * 32, col, 0)
    screen.render(xo + 0, yo + 8, xt + (yt + 1) * 32, col, 0)
    screen.render(xo + 8, yo + 8, xt + 1 + (yt + 1) * 32, col, 0)
}

fun Slime.touchedBy(entity: Entity) {
    if (entity is Player) {
        entity.hurt(this, lvl, dir)
    }
}