package entity

import game.*
import gfx.*
import input.InputHandler
import item.*
import level.*
import level.tile.*
import screen.*
import sound.*
import util.uniqueRandom
import kotlin.random.Random

data class Player(
    override var x: Int = 24,
    override var y: Int = 24,
    override var walkDist: Int = 0,
    override var dir: Int = 0,
    override var hurtTime: Int = 0,
    override var xKnockback: Int = 0,
    override var yKnockback: Int = 0,
    override var maxHealth: Int = 10,
    override var health: Int = maxHealth,
    override var swimTimer: Int = 0,
    override var tickTime: Int = 0,
    override var xr: Int = 6,
    override var yr: Int = 6,
    override var removed: Boolean = false,
    var attackDir: Int = -1,
    var attackTime: Int = 0,
    var attackItem: Item? = null,
    var activeItem: Item? = null,
    var maxStamina: Int = 10,
    var stamina: Int = maxStamina,
    var staminaRecharge: Int = 0,
    var staminaRechargeDelay: Int = 0,
    var score: Int = 0,
    var onStairDelay: Int = 0,
    var invulnerableTime: Int = 0,
) : Mob {
    override lateinit var level: Level
    override val random: Random = uniqueRandom()
    var inventory: Inventory = Inventory()

    init {
        inventory.add(FurnitureItem(Workbench()))
        inventory.add(PowerGloveItem())
    }
}

fun Player.tick() {
    (this as Mob).tick()
    if (invulnerableTime > 0) invulnerableTime--
    val onTile = level.getTile(x shr 4, y shr 4)
    if (onTile == Tile.stairsDown || onTile == Tile.stairsUp) {
        if (onStairDelay == 0) {
            changeLevel(if (onTile == Tile.stairsUp) 1 else -1)
            onStairDelay = 10
            return
        }
        onStairDelay = 10
    } else if (onStairDelay > 0) {
        onStairDelay--
    }

    if (stamina <= 0 && staminaRechargeDelay == 0 && staminaRecharge == 0) {
        staminaRechargeDelay = 40
    }

    if (staminaRechargeDelay > 0) {
        staminaRechargeDelay--
    }

    if (staminaRechargeDelay == 0) {
        staminaRecharge++
        if (isSwimming()) {
            staminaRecharge = 0
        }
        while (staminaRecharge > 10) {
            staminaRecharge -= 10
            if (stamina < maxStamina) stamina++
        }
    }

    var xa = 0
    var ya = 0
    if (InputHandler.up.down) ya--
    if (InputHandler.down.down) ya++
    if (InputHandler.left.down) xa--
    if (InputHandler.right.down) xa++
    if (isSwimming() && tickTime % 60 == 0) {
        if (stamina > 0) {
            stamina--
        } else {
            hurt(this, 1, dir xor 1)
        }
    }

    if (staminaRechargeDelay % 2 == 0) {
        move(xa, ya)
    }

    if (InputHandler.attack.clicked) {
        if (stamina > 0) {
            stamina--
            staminaRecharge = 0
            attack()
        }
    }
    if (InputHandler.menu.clicked) {
        if (!use()) {
            Game.menu = InventoryMenu(this)
        }
    }

    if (attackTime > 0) --attackTime
}

fun Player.use(): Boolean {
    val yo = -2
    return when (dir) {
        0 -> use(x - 8, y + 4 + yo, x + 8, y + 12 + yo)
        1 -> use(x - 8, y - 12 + yo, x + 8, y - 4 + yo)
        3 -> use(x + 4, y - 8 + yo, x + 12, y + 8 + yo)
        2 -> use(x - 12, y - 8 + yo, x - 4, y + 8 + yo)
        else -> false
    }
}

fun Player.use(x0: Int, y0: Int, x1: Int, y1: Int): Boolean {
    val entities = level.getEntities(x0, y0, x1, y1)
    return entities.any { e -> e != this && e.use(this, attackDir) }
}

fun Player.attack() {
    walkDist += 8
    attackDir = dir
    attackItem = activeItem

    activeItem?.let { item ->
        attackTime = 10
        val yo = -2
        val range = 12
        if (when (dir) {
                0 -> interact(x - 8, y + 4 + yo, x + 8, y + range + yo)
                1 -> interact(x - 8, y - range + yo, x + 8, y - 4 + yo)
                3 -> interact(x + 4, y - 8 + yo, x + range, y + 8 + yo)
                2 -> interact(x - range, y - 8 + yo, x - 4, y + 8 + yo)
                else -> false
            }
        ) return

        var xt = x shr 4
        var yt = (y + yo) shr 4
        when (attackDir) {
            0 -> yt = (y + range + yo) shr 4
            1 -> yt = (y - range + yo) shr 4
            2 -> xt = (x - range) shr 4
            3 -> xt = (x + range) shr 4
        }

        if (xt in 0 until level.w && yt in 0 until level.h) {
            level.getTile(xt, yt)?.let { tile ->

                if (item.interactOn(
                        tile, level, xt, yt, this, attackDir
                    ) || tile.interact(level, xt, yt, this, item, attackDir)
                ) {
                    return
                }
            }
            if (item.isDepleted()) activeItem = null
        }
    }

    if (activeItem?.canAttack() != false) {
        attackTime = 5
        val yo = -2
        val range = 20
        when (dir) {
            0 -> hurt(x - 8, y + 4 + yo, x + 8, y + range + yo)
            1 -> hurt(x - 8, y - range + yo, x + 8, y - 4 + yo)
            3 -> hurt(x + 4, y - 8 + yo, x + range, y + 8 + yo)
            2 -> hurt(x - range, y - 8 + yo, x - 4, y + 8 + yo)
        }

        var xt = x shr 4
        var yt = (y + yo) shr 4
        when (attackDir) {
            0 -> yt = (y + range + yo) shr 4
            1 -> yt = (y - range + yo) shr 4
            2 -> xt = (x - range) shr 4
            3 -> xt = (x + range) shr 4
        }

        if (xt in 0 until level.w && yt in 0 until level.h) {
            level.getTile(xt, yt)?.hurt(level, xt, yt, this, random.nextInt(3) + 1, attackDir)
        }
    }
}

fun Player.interact(x0: Int, y0: Int, x1: Int, y1: Int): Boolean {
    val entities = level.getEntities(x0, y0, x1, y1)
    return entities.any { e ->
        e != this && e.interact(
            this, activeItem!!, attackDir
        )
    }
}

fun Player.hurt(x0: Int, y0: Int, x1: Int, y1: Int) {
    val entities = level.getEntities(x0, y0, x1, y1)
    entities.forEach { e ->
        if (e != this) e.hurt(this, getAttackDamage(e), attackDir)
    }
}

fun Player.getAttackDamage(e: Entity): Int {
    var dmg = random.nextInt(3) + 1
    attackItem?.let { dmg += it.getAttackDamageBonus(e) }
    return dmg
}

fun Player.render(screen: Screen) {
    var xt = 0
    var yt = 14

    var flip1: Int = (walkDist shr 3) and 1
    var flip2: Int = (walkDist shr 3) and 1

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

    val xo: Int = x - 8
    var yo: Int = y - 11
    if (isSwimming()) {
        yo += 4
        var waterColor = Color.get(-1, -1, 115, 335)
        if (tickTime / 8 % 2 == 0) {
            waterColor = Color.get(-1, 335, 5, 115)
        }
        screen.render(xo + 0, yo + 3, 5 + 13 * 32, waterColor, 0)
        screen.render(xo + 8, yo + 3, 5 + 13 * 32, waterColor, 1)
    }


    var col = Color.get(-1, 100, 220, 532)
    if (hurtTime > 0) {
        col = Color.get(-1, 555, 555, 555)
    }

    if (activeItem is FurnitureItem) {
        yt += 2
    }

    screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1)
    screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1)

    if (!isSwimming()) {
        screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2)
        screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2)
    }

    if (attackTime > 0 && attackDir == 0) {
        screen.render(xo + 0, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 2)
        screen.render(xo + 8, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 3)
        attackItem?.renderIcon(screen, xo + 4, yo + 8 + 4)
    }
    if (attackTime > 0 && attackDir == 1) {
        screen.render(xo + 0, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 0)
        screen.render(xo + 8, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 1)
        attackItem?.renderIcon(screen, xo + 4, yo - 4)
    }
    if (attackTime > 0 && attackDir == 2) {
        screen.render(xo - 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 1)
        screen.render(xo - 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 3)
        attackItem?.renderIcon(screen, xo - 4, yo + 4)
    }
    if (attackTime > 0 && attackDir == 3) {
        screen.render(xo + 8 + 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 0)
        screen.render(xo + 8 + 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 2)
        attackItem?.renderIcon(screen, xo + 8 + 4, yo + 4)
    }

    if (activeItem is FurnitureItem) {
        val furniture = (activeItem as FurnitureItem).furniture
        furniture.x = x
        furniture.y = yo
        furniture.render(screen)
    }
}

fun Player.touchItem(itemEntity: ItemEntity) {
    itemEntity.take(this)
    inventory.add(itemEntity.item)
}

fun Player.canSwim(): Boolean = true

fun Player.findStartPos(level: Level): Boolean {
    while (true) {
        val x = random.nextInt(level.w)
        val y = random.nextInt(level.h)
        if (level.getTile(x, y) == Tile.grass) {
            this.x = x * 16 + 8
            this.y = y * 16 + 8
            return true
        }
    }
}

fun Player.payStamina(cost: Int): Boolean {
    if (cost > stamina) return false
    stamina -= cost
    return true
}

fun Player.changeLevel(dir: Int) {
    Game.scheduleLevelChange(dir)
}

fun Player.getLightRadius(): Int {
    var r = 2
    activeItem?.let { item ->
        if (item is FurnitureItem) {
            val rr = item.furniture.getLightRadius()
            if (rr > r) r = rr
        }
    }
    return r
}

fun Player.die() {
    (this as Mob).die()
    Sound.playerDeath.play()
}

fun Player.touchedBy(entity: Entity) {
    if (entity !is Player) {
        entity.touchedBy(this)
    }
}

fun Player.doHurt(damage: Int, attackDir: Int) {
    if (hurtTime > 0 || invulnerableTime > 0) return
    Sound.playerHurt.play()
    level.add(TextParticle("$damage", x, y, Color.get(-1, 504, 504, 504)))
    health -= damage
    when (attackDir) {
        0 -> yKnockback = 6
        1 -> yKnockback = -6
        2 -> xKnockback = -6
        3 -> xKnockback = 6
    }
    hurtTime = 10
    invulnerableTime = 30
}

fun Player.gameWon() {
    invulnerableTime = 60 * 5
    Game.won()
}