package entity

import gfx.Color
import level.Level
import util.uniqueRandom
import kotlin.random.Random

data class Lantern(
    override var x: Int = 0,
    override var y: Int = 0,
    override val color: Int = Color.get(-1, 0, 111, 555),
    override var pushTime: Int = 0,
    override var pushDir: Int = - 1,
    override val sprite: Int = 5,
    override val name: String = "Lantern",
    override var shouldTake: Player? = null,
    override var xr: Int = 3,
    override var yr: Int = 2,
    override var removed: Boolean = false,
    val lightRadius: Int = 8
) : Furniture {
    override lateinit var level: Level
    override val random: Random = uniqueRandom()
}
