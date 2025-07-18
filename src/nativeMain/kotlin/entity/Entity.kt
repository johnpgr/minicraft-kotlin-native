package entity

import gfx.*
import item.*
import level.*
import level.tile.*
import kotlin.random.Random

sealed interface Entity {
    val random: Random
    var x: Int
    var y: Int
    var xr: Int
    var yr: Int
    var removed: Boolean
    var level: Level
}

fun Entity.render(screen: Screen) {
    when (this) {
        is Player -> render(screen)
        is Zombie -> render(screen)
        is Furniture -> render(screen)
        is ItemEntity -> render(screen)
        is AirWizard -> render(screen)
        is SmashParticle -> render(screen)
        is TextParticle -> render(screen)
        is Slime -> render(screen)
        is Spark -> render(screen)
    }
}

fun Entity.tick() {
    when (this) {
        is Furniture -> tick()
        is Player -> tick()
        is Zombie -> tick()
        is ItemEntity -> tick()
        is AirWizard -> tick()
        is Slime -> tick()
        is SmashParticle -> tick()
        is Spark -> tick()
        is TextParticle -> tick()
    }
}

fun Entity.remove() {
    removed = true
}

fun Entity.init(level: Level) {
    this.level = level
}

fun Entity.intersects(x0: Int, y0: Int, x1: Int, y1: Int): Boolean {
    return !(x + xr < x0 || y + yr < y0 || x - xr > x1 || y - yr > y1)
}

fun Entity.blocks(e: Entity): Boolean {
    return when (this){
        is Furniture -> blocks(e)
        is Mob -> blocks(e)
        else -> false
    }
}

fun Entity.hurt(mob: Mob, dmg: Int, attackDir: Int) {
    when (this){
        is Player -> hurt(mob, dmg, attackDir)
        is Mob -> hurt(mob, dmg, attackDir)
        else -> {}
    }
}

fun Entity.hurt(tile: Tile, x: Int, y: Int, dmg: Int) {
    when (this){
        is Player -> hurt(tile, x, y, dmg)
        is Mob -> hurt(tile, x, y, dmg)
        else -> {}
    }
}

fun Entity.move(xa: Int, ya: Int): Boolean {
    if (xa != 0 || ya != 0) {
        var stopped = true
        if (xa != 0 && move2(xa, 0)) stopped = false
        if (ya != 0 && move2(0, ya)) stopped = false
        if (!stopped) {
            val xt = x shr 4
            val yt = y shr 4
            level.getTile(xt, yt)?.steppedOn(level, xt, yt, this)
        }
        return !stopped
    }
    return true
}

fun Entity.move2(xa: Int, ya: Int): Boolean {
    require(xa == 0 || ya == 0) { "Move2 can only move along one axis at a time!" }

    val xto0 = (x - xr) shr 4
    val yto0 = (y - yr) shr 4
    val xto1 = (x + xr) shr 4
    val yto1 = (y + yr) shr 4

    val xt0 = (x + xa - xr) shr 4
    val yt0 = (y + ya - yr) shr 4
    val xt1 = (x + xa + xr) shr 4
    val yt1 = (y + ya + yr) shr 4

    var blocked = false
    for (yt in yt0..yt1) {
        for (xt in xt0..xt1) {
            if (xt in xto0..xto1 && yt in yto0..yto1) continue
            level.getTile(xt, yt)?.bumpedInto(level, xt, yt, this)
            if (level.getTile(xt, yt)?.mayPass(level, xt, yt, this) == false) {
                blocked = true
                return false
            }
        }
    }
    if (blocked) return false

    val wasInside = level.getEntities(x - xr, y - yr, x + xr, y + yr)
    val isInside =
        level.getEntities(x + xa - xr, y + ya - yr, x + xa + xr, y + ya + yr)
    isInside.forEach { e ->
        if (e != this) e.touchedBy(this)
    }
    isInside.filter { it !in wasInside }.forEach { e ->
        if (e != this && e.blocks(this)) return false
    }

    x += xa
    y += ya
    return true
}

fun Entity.touchedBy(entity: Entity) {
    when (this) {
        is Anvil -> touchedBy(entity)
        is Chest -> touchedBy(entity)
        is Furnace -> touchedBy(entity)
        is Lantern -> touchedBy(entity)
        is Oven -> touchedBy(entity)
        is Workbench -> touchedBy(entity)
        is ItemEntity -> touchedBy(entity)
        is AirWizard -> touchedBy(entity)
        is Player -> touchedBy(entity)
        is Slime -> touchedBy(entity)
        is Zombie -> touchedBy(entity)
        else -> {}
    }
}

fun Entity.isBlockableBy(mob: Mob): Boolean {
    return when (this){
        is ItemEntity -> isBlockableBy(mob)
        is Spark -> isBlockableBy(mob)
        else -> true
    }
}

fun Entity.touchItem(itemEntity: ItemEntity) {
    when (this) {
        is Player -> touchItem(itemEntity)
        else -> {}
    }
}

fun Entity.canSwim(): Boolean {
    return when (this){
        is Player -> canSwim()
        else -> false
    }
}

fun Entity.interact(player: Player, item: Item, attackDir: Int): Boolean {
    return when (this) {
        is Player -> interact(player, item, attackDir)
        else -> item.interact(player, this, attackDir)
    }
}

fun Entity.use(player: Player, attackDir: Int): Boolean {
    return when (this) {
        is Player -> use(player, attackDir)
        is Anvil -> use(player, attackDir)
        is Chest -> use(player, attackDir)
        is Furnace -> use(player, attackDir)
        is Oven -> use(player, attackDir)
        is Workbench -> use(player, attackDir)
        else -> false
    }
}

fun Entity.getLightRadius(): Int {
    return when (this) {
        is Lantern -> lightRadius
        is Player -> getLightRadius()
        else -> 0
    }
}