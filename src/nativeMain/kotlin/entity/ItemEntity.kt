package entity

import item.*
import level.*
import gfx.*
import sound.*
import util.*
import kotlin.random.Random

data class ItemEntity(
    var item: Item,
    override var x: Int,
    override var y: Int,
    override var xr: Int = 3,
    override var yr: Int = 3,
    override var removed: Boolean = false,
    var lifeTime: Int = 0,
    var walkDist: Int = 0,
    var dir: Int = 0,
    var hurtTime: Int = 0,
    var xKnockback: Int = 0,
    var yKnockback: Int = 0,
    var xa: Double = 0.0,
    var ya: Double = 0.0,
    var za: Double = 0.0,
    var xx: Double = x.toDouble(),
    var yy: Double = y.toDouble(),
    var zz: Double = 2.0,
    var time: Int = 0,
) : Entity{
    override lateinit var level: Level
    override val random: Random = uniqueRandom()

    init {
        xa = random.nextGaussian() * 0.3
        ya = random.nextGaussian() * 0.2
        za = random.nextFloat() * 0.7 + 1
        lifeTime = 60 * 10 + random.nextInt(60)
    }
}

fun ItemEntity.tick() {
    time++
    if (time >= lifeTime) {
        remove()
        return
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
    val ox = x
    val oy = y
    val nx = xx.toInt()
    val ny = yy.toInt()
    val expectedX = nx - x
    val expectedY = ny - y
    move(nx - x, ny - y)
    val gotX = x - ox
    val gotY = y - oy
    xx += (gotX - expectedX).toDouble()
    yy += (gotY - expectedY).toDouble()

    if (hurtTime > 0) hurtTime--
}

fun ItemEntity.isBlockableBy(mob: Mob?): Boolean {
    return false
}

fun ItemEntity.render(screen: Screen) {
    if (time >= lifeTime - 6 * 20) {
        if (time / 6 % 2 == 0) return
    }
    screen.render(x - 4, y - 4, item.sprite, Color.get(-1, 0, 0, 0), 0)
    screen.render(
        x - 4,
        y - 4 - (zz).toInt(),
        item.sprite,
        item.color,
        0
    )
}

fun ItemEntity.touchedBy(entity: Entity) {
    if (time > 30) entity.touchItem(this)
}

fun ItemEntity.take(player: Player) {
    Sound.pickup.play()
    player.score++
    item.onTake(this)
    remove()
}
