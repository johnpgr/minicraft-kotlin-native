package entity

import gfx.*
import level.*
import level.tile.*
import sound.*
import kotlin.random.Random

sealed interface Mob : Entity {
    var walkDist: Int
    var dir: Int
    var hurtTime: Int
    var xKnockback: Int
    var yKnockback: Int
    var maxHealth: Int
    var health: Int
    var swimTimer: Int
    var tickTime: Int
}

fun Mob.tick() {
    tickTime++
    if (level.getTile(x shr 4, y shr 4) == Tile.lava) {
        hurt(this, 4, dir xor 1)
    }
    if (health <= 0) {
        die()
    }
    if (hurtTime > 0) hurtTime--
}

fun Mob.die() {
    remove()
}

fun Mob.move(xa: Int, ya: Int): Boolean {
    if (isSwimming()) {
        if (swimTimer++ % 2 == 0) return true
    }
    if (xKnockback < 0) {
        move2(-1, 0)
        xKnockback++
    }
    if (xKnockback > 0) {
        move2(1, 0)
        xKnockback--
    }
    if (yKnockback < 0) {
        move2(0, -1)
        yKnockback++
    }
    if (yKnockback > 0) {
        move2(0, 1)
        yKnockback--
    }
    if (hurtTime > 0) return true
    if (xa != 0 || ya != 0) {
        walkDist++
        when {
            xa < 0 -> dir = 2
            xa > 0 -> dir = 3
            ya < 0 -> dir = 1
            ya > 0 -> dir = 0
        }
    }
    return (this as Entity).move(xa, ya)
}

fun Mob.isSwimming(): Boolean {
    val tile = level?.getTile(x shr 4, y shr 4)
    return tile == Tile.water || tile == Tile.lava
}

fun Mob.blocks(e: Entity): Boolean = e.isBlockableBy(this)

fun Mob.hurt(tile: Tile, x: Int, y: Int, damage: Int) {
    doHurt(damage, dir xor 1)
}

fun Mob.hurt(mob: Mob, damage: Int, attackDir: Int) {
    doHurt(damage, attackDir)
}

fun Mob.heal(heal: Int) {
    if (hurtTime > 0) return
    level.add(TextParticle("$heal", x, y, Color.get(-1, 50, 50, 50)))
    health = minOf(health + heal, maxHealth)
}

fun Mob.doHurt(damage: Int, attackDir: Int) {
    if (hurtTime > 0) return
    level.player?.let { player ->
        val xd = player.x - x
        val yd = player.y - y
        if (xd * xd + yd * yd < 80 * 80) {
            Sound.monsterHurt.play()
        }
    }
    level.add(TextParticle("$damage", x, y, Color.get(-1, 500, 500, 500)))
    health -= damage
    when (attackDir) {
        0 -> yKnockback = 6
        1 -> yKnockback = -6
        2 -> xKnockback = -6
        3 -> xKnockback = 6
    }
    hurtTime = 10
}

fun Mob.findStartPos(level: Level): Boolean {
    val x = random.nextInt(level.w)
    val y = random.nextInt(level.h)
    val xx = x * 16 + 8
    val yy = y * 16 + 8

    level.player?.let { player ->
        val xd = player.x - xx
        val yd = player.y - yy
        if (xd * xd + yd * yd < 80 * 80) return false
    }

    val r = level.monsterDensity * 16
    if (level.getEntities(xx - r, yy - r, xx + r, yy + r)
            .isNotEmpty()
    ) return false

    level.getTile(x, y)?.let { tile ->
        if (tile.mayPass(level, x, y, this)) {
            this.x = xx
            this.y = yy
            return true
        }
    }
    return false
}

