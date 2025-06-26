package item

data class ToolType(val name: String, val sprite: Int) {
    companion object {
        val shovel = ToolType("Shvl", 0)
        val hoe = ToolType("Hoe", 1)
        val sword = ToolType("Swrd", 2)
        val pickaxe = ToolType("Pick", 3)
        val axe = ToolType("Axe", 4)
    }
}