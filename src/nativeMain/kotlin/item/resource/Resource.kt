package item.resource

import entity.Player
import gfx.Color
import level.Level
import level.tile.Tile

sealed interface Resource {
    val name: String
    val sprite: Int
    val color: Int

    companion object {
        val wood: Resource =
            BasicResource("Wood", 1 + 4 * 32, Color.get(-1, 200, 531, 430))
        val stone: Resource =
            BasicResource("Stone", 2 + 4 * 32, Color.get(-1, 111, 333, 555))
        val flower: Resource = PlantableResource(
            "Flower",
            0 + 4 * 32,
            Color.get(-1, 10, 444, 330),
            Tile.flower,
            Tile.grass
        )
        val acorn: Resource = PlantableResource(
            "Acorn",
            3 + 4 * 32,
            Color.get(-1, 100, 531, 320),
            Tile.treeSapling,
            Tile.grass
        )
        val dirt: Resource = PlantableResource(
            "Dirt",
            2 + 4 * 32,
            Color.get(-1, 100, 322, 432),
            Tile.dirt,
            Tile.hole,
            Tile.water,
            Tile.lava
        )
        val sand: Resource = PlantableResource(
            "Sand",
            2 + 4 * 32,
            Color.get(-1, 110, 440, 550),
            Tile.sand,
            Tile.grass,
            Tile.dirt
        )
        val cactusFlower: Resource = PlantableResource(
            "Cactus",
            4 + 4 * 32,
            Color.get(-1, 10, 40, 50),
            Tile.cactusSapling,
            Tile.sand
        )
        val seeds: Resource = PlantableResource(
            "Seeds",
            5 + 4 * 32,
            Color.get(-1, 10, 40, 50),
            Tile.wheat,
            Tile.farmland
        )
        val wheat: Resource =
            BasicResource("Wheat", 6 + 4 * 32, Color.get(-1, 110, 330, 550))
        val bread: Resource = FoodResource(
            "Bread", 8 + 4 * 32, Color.get(-1, 110, 330, 550), 2, 5
        )
        val apple: Resource = FoodResource(
            "Apple", 9 + 4 * 32, Color.get(-1, 100, 300, 500), 1, 5
        )

        val coal: Resource =
            BasicResource("COAL", 10 + 4 * 32, Color.get(-1, 0, 111, 111))
        val ironOre: Resource =
            BasicResource("I.ORE", 10 + 4 * 32, Color.get(-1, 100, 322, 544))
        val goldOre: Resource =
            BasicResource("G.ORE", 10 + 4 * 32, Color.get(-1, 110, 440, 553))
        val ironIngot: Resource =
            BasicResource("IRON", 11 + 4 * 32, Color.get(-1, 100, 322, 544))
        val goldIngot: Resource =
            BasicResource("GOLD", 11 + 4 * 32, Color.get(-1, 110, 330, 553))

        val slime: Resource =
            BasicResource("SLIME", 10 + 4 * 32, Color.get(-1, 10, 30, 50))
        val glass: Resource =
            BasicResource("glass", 12 + 4 * 32, Color.get(-1, 555, 555, 555))
        val cloth: Resource =
            BasicResource("cloth", 1 + 4 * 32, Color.get(-1, 25, 252, 141))
        val cloud: Resource = PlantableResource(
            "cloud",
            2 + 4 * 32,
            Color.get(-1, 222, 555, 444),
            Tile.cloud,
            Tile.infiniteFall
        )
        val gem: Resource =
            BasicResource("gem", 13 + 4 * 32, Color.get(-1, 101, 404, 545))
    }
}

data class BasicResource(
    override val name: String, override val sprite: Int, override val color: Int
) : Resource {
    init {
        if (name.length > 6) error("Name cannot be longer than six characters!")
    }
}

fun Resource.interactOn(
    tile: Tile, level: Level, xt: Int, yt: Int, player: Player, attackDir: Int
): Boolean {
    return when (this) {
        is BasicResource -> false
        is PlantableResource -> interactOn( tile, level, xt, yt, player, attackDir)
        is FoodResource -> interactOn(tile, level, xt, yt, player, attackDir)
    }
}
