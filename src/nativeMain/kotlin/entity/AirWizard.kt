package entity

import gfx.*
import level.*
import sound.*
import kotlin.math.*
import util.uniqueRandom
import kotlin.random.Random

data class AirWizard(
    override var walkDist: Int = 0,
    override var dir: Int = 0,
    override var hurtTime: Int = 0,
    override var xKnockback: Int = 0,
    override var yKnockback: Int = 0,
    override var maxHealth: Int = 2000,
    override var health: Int = maxHealth,
    override var swimTimer: Int = 0,
    override var tickTime: Int = 0,
    override var x: Int = 0,
    override var y: Int = 0,
    override var xr: Int = 4,
    override var yr: Int = 3,
    override var removed: Boolean = false,
    var xa: Int = 0,
    var ya: Int = 0,
    var randomWalkTime: Int = 0,
    var attackDelay: Int = 0,
    var attackTime: Int = 0,
    var attackType: Int = 0,
) : Mob {
    override lateinit var level: Level
    override val random: Random = uniqueRandom()

    init {
        x = random.nextInt(64 * 16)
        y = random.nextInt(64 * 16)
    }
}

fun AirWizard.tick() {
    (this as Mob).tick()

    if (attackDelay > 0) {
        dir = (attackDelay - 45) / 4 % 4
        dir = (dir * 2 % 4) + (dir / 2)
        if (attackDelay < 45) {
            dir = 0
        }
        attackDelay--
        if (attackDelay == 0) {
            attackType = 0
            if (health < 1000) attackType = 1
            if (health < 200) attackType = 2
            attackTime = 60 * 2
        }
        return
    }

    if (attackTime > 0) {
        attackTime--
        val dir: Double = attackTime * 0.25 * (attackTime % 2 * 2 - 1)
        val speed: Double = (0.7) + attackType * 0.2
        level.add(Spark(this, cos(dir) * speed, sin(dir) * speed))
        return
    }

    level.player?.let { player ->
        if (randomWalkTime == 0) {
            val xd = player.x - x
            val yd = player.y - y
            if (xd * xd + yd * yd < 32 * 32) {
                xa = 0
                ya = 0
                if (xd < 0) xa = +1
                if (xd > 0) xa = -1
                if (yd < 0) ya = +1
                if (yd > 0) ya = -1
            } else if (xd * xd + yd * yd > 80 * 80) {
                xa = 0
                ya = 0
                if (xd < 0) xa = -1
                if (xd > 0) xa = +1
                if (yd < 0) ya = -1
                if (yd > 0) ya = +1
            }
        }
    }

    val speed = if ((tickTime % 4) == 0) 0 else 1
    if (!move(xa * speed, ya * speed) || random.nextInt(100) == 0) {
        randomWalkTime = 30
        xa = (random.nextInt(3) - 1)
        ya = (random.nextInt(3) - 1)
    }
    if (randomWalkTime > 0) {
        randomWalkTime--
        level.player?.let { player ->
           if(randomWalkTime == 0) {
               val xd = player.x - x
               val yd = player.y - y
               if (random.nextInt(4) == 0 && xd * xd + yd * yd < 50 * 50) {
                   if (attackDelay == 0 && attackTime == 0) {
                       attackDelay = 60 * 2
                   }
               }
           }
        }
    }
}

fun AirWizard.doHurt(damage: Int, attackDir: Int) {
    (this as Mob).doHurt(damage, attackDir)
    if (attackDelay == 0 && attackTime == 0) {
        attackDelay = 60 * 2
    }
}

fun AirWizard.render(screen: Screen) {
    var xt = 8
    val yt = 14

    var flip1 = (walkDist shr 3) and 1
    var flip2 = (walkDist shr 3) and 1

    if (dir == 1) {
        xt += 2
    }
    if (dir > 1) {
        flip1 = 0
        flip2 = ((walkDist shr 4) and 1)
        if (dir == 2) {
            flip1 = 1
        }
        xt += 4 + ((walkDist shr 3) and 1) * 2
    }

    val xo = x - 8
    val yo = y - 11

    var col1: Int = Color.get(-1, 100, 500, 555)
    var col2: Int = Color.get(-1, 100, 500, 532)
    if (health < 200) {
        if (tickTime / 3 % 2 == 0) {
            col1 = Color.get(-1, 500, 100, 555)
            col2 = Color.get(-1, 500, 100, 532)
        }
    } else if (health < 1000) {
        if (tickTime / 5 % 4 == 0) {
            col1 = Color.get(-1, 500, 100, 555)
            col2 = Color.get(-1, 500, 100, 532)
        }
    }
    if (hurtTime > 0) {
        col1 = Color.get(-1, 555, 555, 555)
        col2 = Color.get(-1, 555, 555, 555)
    }

    screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col1, flip1)
    screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col1, flip1)
    screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col2, flip2)
    screen.render(
        xo + 8 - 8 * flip2,
        yo + 8,
        xt + 1 + (yt + 1) * 32,
        col2,
        flip2
    )
}

fun AirWizard.touchedBy(entity: Entity) {
    if (entity is Player) {
        entity.hurt(this, 3, dir)
    }
}

fun AirWizard.die() {
    (this as Mob).die()
    level.player?.let { player ->
        player.score += 1000
        player.gameWon()
    }
    Sound.bossDeath.play()
}
