package entity

import gfx.Color
import level.Level
import screen.ContainerMenu
import util.uniqueRandom
import kotlin.random.Random

data class Chest(
    override var x: Int = 0,
    override var y: Int = 0,
    override var pushTime: Int = 0,
    override var pushDir: Int = -1,
    override val color: Int = Color.get(-1, 110, 331, 552),
    override val sprite: Int = 1,
    override val name: String = "Chest",
    override var shouldTake: Player? = null,
    override var xr: Int = 3,
    override var yr: Int = 3,
    override var removed: Boolean = false,
    var inventory: Inventory = Inventory()
) : Furniture {
    override val random: Random = uniqueRandom()
    override lateinit var level: Level
}

fun Chest.use(player: Player, attackDir: Int): Boolean {
    Game.menu = ContainerMenu(player, "Chest", inventory)
    return true
}
