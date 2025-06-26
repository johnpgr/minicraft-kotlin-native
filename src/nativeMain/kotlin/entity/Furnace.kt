package entity

import crafting.Crafting
import gfx.*
import level.*
import screen.CraftingMenu
import util.uniqueRandom
import kotlin.random.Random

data class Furnace(
    override var x: Int = 0,
    override var y: Int = 0,
    override var pushTime: Int = 0,
    override var pushDir: Int = -1,
    override val color: Int = Color.get(-1, 0, 222, 333),
    override val sprite: Int = 3,
    override val name: String = "Furnace",
    override var shouldTake: Player? = null,
    override var xr: Int = 3,
    override var yr: Int = 2,
    override var removed: Boolean = false,
) : Furniture {
    override val random: Random = uniqueRandom()
    override lateinit var level: Level
}

fun Furniture.use(player: Player, attackDir: Int): Boolean {
    Game.menu = CraftingMenu(Crafting.furnaceRecipes, player)
    return true
}
