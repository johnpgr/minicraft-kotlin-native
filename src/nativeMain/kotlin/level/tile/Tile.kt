package level.tile

import entity.*
import gfx.*
import item.*
import item.resource.Resource
import kotlin.random.Random
import level.Level

sealed interface Tile {
    val id: Byte
    val random: Random
    var connectsToGrass: Boolean
    var connectsToSand: Boolean
    var connectsToLava: Boolean
    var connectsToWater: Boolean

    companion object {
        var tickCount: Int = 0
        var tiles: Array<Tile?> = arrayOfNulls<Tile>(256)

        val grass: Tile = GrassTile(0)
        val rock: Tile = RockTile(1)
        val water: Tile = WaterTile(2)
        val flower: Tile = FlowerTile(3)
        val tree: Tile = TreeTile(4)
        val dirt: Tile = DirtTile(5)
        val sand: Tile = SandTile(6)
        val cactus: Tile = CactusTile(7)
        val hole: Tile = HoleTile(8)
        val treeSapling: Tile = SaplingTile(9, grass, tree)
        val cactusSapling: Tile = SaplingTile(10, sand, cactus)
        val farmland: Tile = FarmTile(11)
        val wheat: Tile = WheatTile(12)
        val lava: Tile = LavaTile(13)
        val stairsDown: Tile = StairsTile(14, false)
        val stairsUp: Tile = StairsTile(15, true)
        val infiniteFall: Tile = InfiniteFallTile(16)
        val cloud: Tile = CloudTile(17)
        val hardRock: Tile = HardRockTile(18)
        val ironOre: Tile = OreTile(19, Resource.ironOre)
        val goldOre: Tile = OreTile(20, Resource.goldOre)
        val gemOre: Tile = OreTile(21, Resource.gem)
        val cloudCactus: Tile = CloudCactusTile(22)
    }
}

fun Tile.render(screen: Screen, level: Level, x: Int, y: Int) {
    when (this) {
        is GrassTile -> render(screen, level, x, y)
        is RockTile -> render(screen, level, x, y)
        is CactusTile -> render(screen, level, x, y)
        is CloudCactusTile -> render(screen, level, x, y)
        is CloudTile -> render(screen, level, x, y)
        is DirtTile -> render(screen, level, x, y)
        is FarmTile -> render(screen, level, x, y)
        is FlowerTile -> render(screen, level, x, y)
        is HardRockTile -> render(screen, level, x, y)
        is HoleTile -> render(screen, level, x, y)
        is InfiniteFallTile -> render(screen, level, x, y)
        is LavaTile -> render(screen, level, x, y)
        is OreTile -> render(screen, level, x, y)
        is SandTile -> render(screen, level, x, y)
        is SaplingTile -> render(screen, level, x, y)
        is StairsTile -> render(screen, level, x, y)
        is TreeTile -> render(screen, level, x, y)
        is WaterTile -> render(screen, level, x, y)
        is WheatTile -> render(screen, level, x, y)
    }
}

fun Tile.mayPass(level: Level, x: Int, y: Int, e: Entity): Boolean {
    return when (this) {
        is CactusTile -> mayPass(level, x, y, e)
        is CloudCactusTile -> mayPass(level, x, y, e)
        is CloudTile -> mayPass(level, x, y, e)
        is HardRockTile -> mayPass(level, x, y, e)
        is HoleTile -> mayPass(level, x, y, e)
        is InfiniteFallTile -> mayPass(level, x, y, e)
        is OreTile -> mayPass(level, x, y, e)
        is RockTile -> mayPass(level, x, y, e)
        is TreeTile -> mayPass(level, x, y, e)
        else -> true
    }
}

fun Tile.getLightRadius(level: Level, x: Int, y: Int): Int {
    return when (this) {
        is LavaTile -> getLightRadius(level, x, y)
        else -> 0
    }
}

fun Tile.hurt(
    level: Level,
    x: Int,
    y: Int,
    source: Mob,
    dmg: Int,
    attackDir: Int
) {
    when (this) {
        is CactusTile -> hurt(level, x, y, source, dmg, attackDir)
        is CloudCactusTile -> hurt(level, x, y, source, dmg, attackDir)
        is FlowerTile -> hurt(level, x, y, source, dmg, attackDir)
        is HardRockTile -> hurt(level, x, y, source, dmg, attackDir)
        is OreTile -> hurt(level, x, y, source, dmg, attackDir)
        is RockTile -> hurt(level, x, y, source, dmg, attackDir)
        is SaplingTile -> hurt(level, x, y, source, dmg, attackDir)
        is TreeTile -> hurt(level, x, y, source, dmg, attackDir)
        is WheatTile -> hurt(level, x, y, source, dmg, attackDir)
        else -> {}
    }
}

fun Tile.bumpedInto(level: Level, xt: Int, yt: Int, entity: Entity) {
    when (this) {
        is CactusTile -> bumpedInto(level, xt, yt, entity)
        is CloudCactusTile -> bumpedInto(level, xt, yt, entity)
        is OreTile -> bumpedInto(level, xt, yt, entity)
        else -> {}
    }
}

fun Tile.tick(level: Level, xt: Int, yt: Int) {
    when (this) {
        is CactusTile -> tick(level, xt, yt)
        is FarmTile -> tick(level, xt, yt)
        is GrassTile -> tick(level, xt, yt)
        is HardRockTile -> tick(level, xt, yt)
        is InfiniteFallTile -> tick(level, xt, yt)
        is LavaTile -> tick(level, xt, yt)
        is RockTile -> tick(level, xt, yt)
        is SandTile -> tick(level, xt, yt)
        is SaplingTile -> tick(level, xt, yt)
        is TreeTile -> tick(level, xt, yt)
        is WaterTile -> tick(level, xt, yt)
        is WheatTile -> tick(level, xt, yt)
        else -> {}
    }
}

fun Tile.steppedOn(level: Level, xt: Int, yt: Int, entity: Entity) {
    when (this) {
        is FarmTile -> steppedOn(level, xt, yt, entity)
        is SandTile -> steppedOn(level, xt, yt, entity)
        is WheatTile -> steppedOn(level, xt, yt, entity)
        else -> {}
    }
}

fun Tile.interact(
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    item: Item,
    attackDir: Int
): Boolean {
    return when (this) {
        is CloudCactusTile -> interact(level, xt, yt, player, item, attackDir)
        is CloudTile -> interact(level, xt, yt, player, item, attackDir)
        is DirtTile -> interact(level, xt, yt, player, item, attackDir)
        is FarmTile -> interact(level, xt, yt, player, item, attackDir)
        is FlowerTile -> interact(level, xt, yt, player, item, attackDir)
        is GrassTile -> interact(level, xt, yt, player, item, attackDir)
        is HardRockTile -> interact(level, xt, yt, player, item, attackDir)
        is OreTile -> interact(level, xt, yt, player, item, attackDir)
        is RockTile -> interact(level, xt, yt, player, item, attackDir)
        is SandTile -> interact(level, xt, yt, player, item, attackDir)
        is TreeTile -> interact(level, xt, yt, player, item, attackDir)
        is WheatTile -> interact(level, xt, yt, player, item, attackDir)
        else -> false
    }
}

fun Tile.use(
    level: Level,
    xt: Int,
    yt: Int,
    player: Player,
    attackDir: Int
): Boolean = false

fun Tile.connectsToLiquid(): Boolean = connectsToWater || connectsToLava

fun Array<Tile?>.add(tile: Tile) {
    if (this[tile.id.toInt()] != null) error("Tile with id ${tile.id} already exists.")
    this[tile.id.toInt()] = tile
}
