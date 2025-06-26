package item

import gfx.*
import level.*
import level.tile.*
import entity.*

sealed interface Item {
    val color: Int
    val sprite: Int
    val name: String
}

fun Item.renderInventory(screen: Screen, i: Int, j: Int) {
    when (this) {
        is FurnitureItem -> renderInventory(screen, i, j)
        is ResourceItem -> renderInventory(screen, i, j)
        is PowerGloveItem -> renderInventory(screen, i, j)
        is ToolItem -> renderInventory(screen, i, j)
    }
}

fun Item.onTake(itemEntity: ItemEntity) {
    when (this) {
        is FurnitureItem -> onTake(itemEntity)
        is ResourceItem -> onTake(itemEntity)
        is ToolItem -> onTake(itemEntity)
        else -> {}
    }
}

fun Item.interact(player: Player, entity: Entity, attackDir: Int): Boolean {
    return when (this) {
        is ToolItem -> interact(player, entity, attackDir)
        is ResourceItem -> interact(player, entity, attackDir)
        is FurnitureItem -> interact(player, entity, attackDir)
        is PowerGloveItem -> interact(player, entity, attackDir)
    }
}

fun Item.renderIcon(screen: Screen, x: Int, y: Int) {
    when (this) {
        is FurnitureItem -> renderIcon(screen, x, y)
        is PowerGloveItem -> renderIcon(screen, x, y)
        is ResourceItem -> renderIcon(screen, x, y)
        is ToolItem -> renderIcon(screen, x, y)
    }
}

fun Item.interactOn(
    tile: Tile,
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    attackDir: Int
): Boolean {
    return when (this) {
        is ResourceItem -> interactOn(tile, level, xt, yt, player, attackDir)
        is FurnitureItem -> interactOn(tile, level, xt, yt, player, attackDir)
        else -> false
    }
}

fun Item.isDepleted(): Boolean {
    return when (this) {
        is FurnitureItem -> isDepleted
        is ResourceItem -> isDepleted
        else -> false
    }
}

fun Item.canAttack(): Boolean {
    return when (this) {
        is ToolItem -> canAttack
        else -> false
    }
}

fun Item.getAttackDamageBonus(e: Entity): Int {
    return when (this) {
        is ToolItem -> getAttackDamageBonus(e)
        else -> 0
    }
}

fun Item.matches(otherItem: Item): Boolean = this::class == otherItem::class