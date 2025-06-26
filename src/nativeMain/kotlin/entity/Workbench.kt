package entity

import crafting.Crafting
import game.Game
import gfx.*
import level.Level
import screen.CraftingMenu
import util.uniqueRandom
import kotlin.random.Random

data class Workbench(
    override var x: Int = 0,
    override var y: Int = 0,
    override var pushTime: Int = 0,
    override var pushDir: Int = 0,
    override val color: Int = Color.get(-1, 100, 321, 431),
    override val sprite: Int = 4,
    override val name: String = "Workbench",
    override var shouldTake: Player? = null,
    override var xr: Int = 3,
    override var yr: Int = 2,
    override var removed: Boolean = false,
) : Furniture {
    override val random: Random = uniqueRandom()
    override lateinit var level: Level
}

fun Workbench.use(player: Player, attackDir: Int): Boolean {
    Game.menu = CraftingMenu(Crafting.workbenchRecipes, player)
    return true
}
