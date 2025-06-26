package entity

import crafting.Crafting
import game.Game
import gfx.*
import level.*
import screen.CraftingMenu
import util.uniqueRandom
import kotlin.random.Random

data class Anvil(
    override var x: Int = 0,
    override var y: Int = 0,
    override var pushTime: Int = 0,
    override var pushDir: Int = -1,
    override val color: Int = Color.get(-1, 0, 111, 222),
    override val sprite: Int = 0,
    override val name: String = "Anvil",
    override var shouldTake: Player? = null,
    override var xr: Int = 3,
    override var yr: Int = 2,
    override var removed: Boolean = false,
) : Furniture {
    override lateinit var level: Level
    override val random: Random = uniqueRandom()
}

fun Anvil.use(player: Player, attackDir: Int): Boolean {
    Game.menu = CraftingMenu(Crafting.anvilRecipes, player)
    return true
}
