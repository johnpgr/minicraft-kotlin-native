package item.resource

import level.*
import entity.Player
import level.tile.Tile

data class PlantableResource(
    override val name: String,
    override val sprite: Int,
    override val color: Int,
    val targetTile: Tile,
    val sourceTiles: List<Tile>,
) : Resource {
    init {
        if (name.length > 6) error("Name cannot be longer than six characters!")
    }

    constructor(
        name: String,
        sprite: Int,
        color: Int,
        targetTile: Tile,
        vararg sourceTiles1: Tile
    ) : this(name, sprite, color, targetTile, sourceTiles1.toList())
}

fun PlantableResource.interactOn(
    tile: Tile, level: Level, xt: Int, yt: Int, player: Player, attackDir: Int
): Boolean {
    if (sourceTiles.contains(tile)) {
        level.setTile(xt, yt, targetTile, 0)
        return true
    }
    return false
}
