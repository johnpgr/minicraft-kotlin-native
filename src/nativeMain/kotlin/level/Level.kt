package level

import entity.AirWizard
import entity.Entity
import entity.Mob
import entity.Player
import entity.Slime
import entity.Zombie
import entity.findStartPos
import entity.getLightRadius
import entity.init
import entity.intersects
import entity.render
import entity.tick
import gfx.Screen
import gfx.renderLight
import level.levelgen.LevelGen
import level.tile.Tile
import level.tile.getLightRadius
import level.tile.render
import level.tile.tick
import kotlin.random.Random
import util.uniqueRandom

data class Level(
    val w: Int, val h: Int, val level: Int, val parentLevel: Level?
) {
    val random: Random = uniqueRandom()

    var tiles: ByteArray
    var data: ByteArray
    var entitiesInTiles: Array<MutableList<Entity>?>

    var grassColor: Int = 141
    var dirtColor: Int = 322
    var sandColor: Int = 550
    val depth: Int
    var monsterDensity: Int = 8

    val entities: MutableList<Entity> = ArrayList()
    val spriteSorter: Comparator<in Entity> = object : Comparator<Entity> {
        override fun compare(a: Entity, b: Entity): Int {
            if (b.y < a.y) return +1
            if (b.y > a.y) return -1
            return 0
        }
    }

    val rowSprites: MutableList<Entity> = ArrayList()
    var player: Player? = null

    init {
        if (level < 0) {
            dirtColor = 222
        }
        depth = level
        val maps: Array<ByteArray>

        if (level == 1) {
            dirtColor = 444
        }
        if (level == 0) maps = LevelGen.createAndValidateTopMap(w, h)
        else if (level < 0) {
            maps = LevelGen.createAndValidateUndergroundMap(w, h, -level)
            monsterDensity = 4
        } else {
            maps = LevelGen.createAndValidateSkyMap(w, h) // Sky level
            monsterDensity = 4
        }

        tiles = maps[0]
        data = maps[1]

        if (parentLevel != null) {
            for (y in 0..<h) for (x in 0..<w) {
                if (parentLevel.getTile(x, y) == Tile.stairsDown) {
                    setTile(x, y, Tile.stairsUp, 0)
                    if (level == 0) {
                        setTile(x - 1, y, Tile.hardRock, 0)
                        setTile(x + 1, y, Tile.hardRock, 0)
                        setTile(x, y - 1, Tile.hardRock, 0)
                        setTile(x, y + 1, Tile.hardRock, 0)
                        setTile(x - 1, y - 1, Tile.hardRock, 0)
                        setTile(x - 1, y + 1, Tile.hardRock, 0)
                        setTile(x + 1, y - 1, Tile.hardRock, 0)
                        setTile(x + 1, y + 1, Tile.hardRock, 0)
                    } else {
                        setTile(x - 1, y, Tile.dirt, 0)
                        setTile(x + 1, y, Tile.dirt, 0)
                        setTile(x, y - 1, Tile.dirt, 0)
                        setTile(x, y + 1, Tile.dirt, 0)
                        setTile(x - 1, y - 1, Tile.dirt, 0)
                        setTile(x - 1, y + 1, Tile.dirt, 0)
                        setTile(x + 1, y - 1, Tile.dirt, 0)
                        setTile(x + 1, y + 1, Tile.dirt, 0)
                    }
                }
            }
        }

        entitiesInTiles = arrayOfNulls(w * h)
        for (i in 0..<w * h) {
            entitiesInTiles[i] = ArrayList()
        }

        if (level == 1) {
            val aw = AirWizard()
            aw.x = w * 8
            aw.y = h * 8
            add(aw)
        }
    }
}

fun Level.renderBackground(screen: Screen, xScroll: Int, yScroll: Int) {
    val xo = xScroll shr 4
    val yo = yScroll shr 4
    val w: Int = (screen.w + 15) shr 4
    val h: Int = (screen.h + 15) shr 4
    screen.xOffset = xScroll
    screen.yOffset = yScroll
    for (y in yo..h + yo) {
        for (x in xo..w + xo) {
            getTile(x, y)?.render(screen, this, x, y)
        }
    }
    screen.xOffset = 0
    screen.yOffset = 0
}


fun Level.renderSprites(screen: Screen, xScroll: Int, yScroll: Int) {
    val xo = xScroll shr 4
    val yo = yScroll shr 4
    val w: Int = (screen.w + 15) shr 4
    val h: Int = (screen.h + 15) shr 4

    screen.xOffset = xScroll
    screen.yOffset = yScroll

    for (y in yo..h + yo) {
        for (x in xo..w + xo) {
            if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue
            entitiesInTiles[x + y * this.w]?.let { rowSprites.addAll(it) }
        }
        if (rowSprites.size > 0) {
            sortAndRender(screen, rowSprites)
        }
        rowSprites.clear()
    }
    screen.xOffset = 0
    screen.yOffset = 0
}

fun Level.renderLight(screen: Screen, xScroll: Int, yScroll: Int) {
    val xo = xScroll shr 4
    val yo = yScroll shr 4
    val w: Int = (screen.w + 15) shr 4
    val h: Int = (screen.h + 15) shr 4

    screen.xOffset = xScroll
    screen.yOffset = yScroll

    val r = 4
    for (y in yo - r..h + yo + r) {
        for (x in xo - r..w + xo + r) {
            if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue
            entitiesInTiles[x + y * this.w]?.let { entities ->
                entities.forEach { entity ->
                    // entity.render(screen);
                    val lr: Int = entity.getLightRadius()
                    if (lr > 0) screen.renderLight(
                        entity.x - 1,
                        entity.y - 4,
                        lr * 8
                    )
                }
            }
            getTile(x, y)?.getLightRadius(this, x, y)?.let { lr ->
                if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8)
            }
        }
    }
    screen.xOffset = 0
    screen.yOffset = 0
}

fun Level.sortAndRender(screen: Screen, list: List<Entity>) {
    val list = list.sortedWith(spriteSorter)
    list.forEach { it.render(screen) }
}

fun Level.getTile(x: Int, y: Int): Tile? {
    if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock
    return Tile.tiles[tiles[x + y * w].toInt()]
}

fun Level.setTile(x: Int, y: Int, t: Tile, dataVal: Int) {
    if (x < 0 || y < 0 || x >= w || y >= h) return
    tiles[x + y * w] = t.id
    data[x + y * w] = dataVal.toByte()
}

fun Level.getData(x: Int, y: Int): Int {
    if (x < 0 || y < 0 || x >= w || y >= h) return 0
    return data[x + y * w].toInt() and 0xff
}

fun Level.setData(x: Int, y: Int, `val`: Int) {
    if (x < 0 || y < 0 || x >= w || y >= h) return
    data[x + y * w] = `val`.toByte()
}

fun Level.add(entity: Entity) {
    if (entity is Player) {
        player = entity
    }

    entity.removed = false
    entities.add(entity)
    entity.init(this)

    insertEntity(entity.x shr 4, entity.y shr 4, entity)
}

fun Level.remove(e: Entity) {
    entities.remove(e)
    val xto: Int = e.x shr 4
    val yto: Int = e.y shr 4
    removeEntity(xto, yto, e)
}

fun Level.insertEntity(x: Int, y: Int, e: Entity) {
    if (x < 0 || y < 0 || x >= w || y >= h) return
    entitiesInTiles[x + y * w]?.add(e)
}

private fun Level.removeEntity(x: Int, y: Int, e: Entity) {
    if (x < 0 || y < 0 || x >= w || y >= h) return
    entitiesInTiles[x + y * w]?.remove(e)
}

fun Level.trySpawn(count: Int) {
    for (i in 0..<count) {
        val mob: Mob

        var minLevel = 1
        var maxLevel = 1
        if (depth < 0) {
            maxLevel = (-depth) + 1
        }
        if (depth > 0) {
            maxLevel = 4
            minLevel = maxLevel
        }

        val lvl: Int = random.nextInt(maxLevel - minLevel + 1) + minLevel
        mob = if (random.nextInt(2) == 0) Slime(lvl)
        else Zombie(lvl)

        if (mob.findStartPos(this)) {
            this.add(mob)
        }
    }
}

fun Level.tick() {
    trySpawn(1)

    for (i in 0..<w * h / 50) {
        val xt: Int = random.nextInt(w)
        val yt: Int = random.nextInt(w)
        getTile(xt, yt)?.tick(this, xt, yt)
    }

    entities.forEachIndexed { i, entity ->
        val xto: Int = entity.x shr 4
        val yto: Int = entity.y shr 4

        entity.tick()

        if (entity.removed) {
            entities.removeAt(i)
            removeEntity(xto, yto, entity)
        } else {
            val xt: Int = entity.x shr 4
            val yt: Int = entity.y shr 4

            if (xto != xt || yto != yt) {
                removeEntity(xto, yto, entity)
                insertEntity(xt, yt, entity)
            }
        }
    }
}

fun Level.getEntities(x0: Int, y0: Int, x1: Int, y1: Int): List<Entity> {
    val result: MutableList<Entity> = ArrayList()
    val xt0 = (x0 shr 4) - 1
    val yt0 = (y0 shr 4) - 1
    val xt1 = (x1 shr 4) + 1
    val yt1 = (y1 shr 4) + 1

    for (y in yt0..yt1) {
        for (x in xt0..xt1) {
            if (x < 0 || y < 0 || x >= w || y >= h) continue
            entitiesInTiles[x + y * this.w]?.let { entities ->
                entities.forEach { entity ->
                    if (entity.intersects(x0, y0, x1, y1)) result.add(entity)
                }
            }
        }
    }

    return result
}
