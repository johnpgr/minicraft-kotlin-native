package entity

import gfx.*
import item.ResourceItem
import item.resource.Resource
import level.Level
import level.add
import kotlin.random.Random

data class Zombie(
    var lvl: Int,
    override var x: Int = 0,
    override var y: Int = 0,
    override var xr: Int = 4,
    override var yr: Int = 3,
    override var removed: Boolean = false,
    override var walkDist: Int = 0,
    override var dir: Int = 0,
    override var hurtTime: Int = 0,
    override var xKnockback: Int = 0,
    override var yKnockback: Int = 0,
    override var maxHealth: Int = lvl * lvl * 10,
    override var health: Int = maxHealth,
    override var swimTimer: Int = 0,
    override var tickTime: Int = 0,
    var xa: Int = 0,
    var ya: Int = 0,
    var randomWalkTime: Int = 0,
) : Mob {
    override val random: Random = util.uniqueRandom()
    override lateinit var level: Level

    init {
        x = random.nextInt(64 * 16)
        y = random.nextInt(64 * 16)
    }
}

fun Zombie.tick() {
    (this as Mob).tick()
    level.player?.let { player ->
        if (randomWalkTime == 0) {
            val xd = player.x - x
            val yd = player.y - y
            if (xd * xd + yd * yd < 50 * 50) {
                xa = 0
                ya = 0
                if (xd < 0) xa = -1
                if (xd > 0) xa = 1
                if (yd < 0) ya = -1
                if (yd > 0) ya = 1
            }
        }
    }

    val speed = tickTime and 1
    if (!move(xa * speed, ya * speed) || random.nextInt(200) == 0) {
        randomWalkTime = 60
        xa = (random.nextInt(3) - 1) * random.nextInt(2)
        ya = (random.nextInt(3) - 1) * random.nextInt(2)
    }
    if (randomWalkTime > 0) randomWalkTime--
}

fun Zombie.render(screen: Screen) {
    var xt = 0
    val yt = 14
    var flip1 = (walkDist shr 3) and 1
    var flip2 = (walkDist shr 3) and 1

    when {
        dir == 1 -> xt += 2
        dir > 1 -> {
            flip1 = 0
            flip2 = (walkDist shr 4) and 1
            if (dir == 2) flip1 = 1
            xt += 4 + ((walkDist shr 3) and 1) * 2
        }
    }

    val xo = x - 8
    val yo = y - 11
    val col = when (lvl) {
        2 -> Color.get(-1, 100, 522, 50)
        3 -> Color.get(-1, 111, 444, 50)
        4 -> Color.get(-1, 0, 111, 20)
        else -> Color.get(-1, 10, 252, 50)
    }
    val finalCol = if (hurtTime > 0) Color.get(-1, 555, 555, 555) else col

    screen.render(xo + 8 * flip1, yo, xt + yt * 32, finalCol, flip1)
    screen.render(xo + 8 - 8 * flip1, yo, xt + 1 + yt * 32, finalCol, flip1)
    screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, finalCol, flip2)
    screen.render(
        xo + 8 - 8 * flip2,
        yo + 8,
        xt + 1 + (yt + 1) * 32,
        finalCol,
        flip2
    )
}

fun Zombie.touchedBy(entity: Entity) {
    if (entity is Player) {
        entity.hurt(this, lvl + 1, dir)
    }
}

fun Zombie.die() {
    (this as Mob).die()
    val count = random.nextInt(2) + 1
    repeat(count) {
        level.add(
            ItemEntity(
                ResourceItem(Resource.cloth),
                x + random.nextInt(11) - 5,
                y + random.nextInt(11) - 5
            )
        )
    }
    level.player?.score += 50 * lvl
}