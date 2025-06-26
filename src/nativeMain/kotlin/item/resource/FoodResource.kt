package item.resource

import entity.*
import level.*
import level.tile.Tile

data class FoodResource(
    override val name: String,
    override val sprite: Int,
    override val color: Int,
    val heal: Int,
    val staminaCost: Int,
) : Resource {
    init {
        if (name.length > 6) error("Name cannot be longer than six characters!")
    }
}

fun FoodResource.interactOn(
    tile: Tile,
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    attackDir: Int
): Boolean {
    if (player.health < player.maxHealth && player.payStamina(staminaCost)) {
        player.heal(heal)
        return true
    }
    return false
}
