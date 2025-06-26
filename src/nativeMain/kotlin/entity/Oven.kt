package entity

import crafting.Crafting
import gfx.Color
import level.Level
import screen.CraftingMenu
import util.uniqueRandom
import kotlin.random.Random

data class Oven(
    override var x: Int = 0,
    override var y: Int = 0,
    override var pushTime: Int = 0,
    override var pushDir: Int = -1,
    override val color: Int = Color.get(-1, 0, 332, 442),
    override val sprite: Int = 2,
    override val name: String = "Oven",
    override var shouldTake: Player? = null,
    override var xr: Int = 3,
    override var yr: Int = 2,
    override var removed: Boolean = false,
) : Furniture {
    override lateinit var level: Level
    override val random: Random = uniqueRandom()
}

fun Oven.use(player: Player, attackDir: Int): Boolean {
    Game.menu = CraftingMenu(Crafting.ovenRecipes, player)
    return true
}
